'use client';
import './dashboard.css';
import React, { useState, useEffect } from 'react';
import { MinusIcon } from 'lucide-react';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import StockChart from './dashboardcomponents/StockChart';

export default function Dashboard() {
  const [watchlist, setWatchlist] = useState([]);
  const [symbol, setSymbol] = useState("");
  const [open, setOpen] = useState(false);
  const [newsItems, setNewsItems] = useState([]);
  const [selectedStock, setSelectedStock] = useState("");
  const SERVER_URL = process.env.NEXT_PUBLIC_SERVER_URL;

  let xsrfTokenCache = null;

  const getXSRFToken = async () => {
    if (xsrfTokenCache) return xsrfTokenCache;
    const res = await fetch(`${SERVER_URL}xsrf`, {
      method: 'GET',
      credentials: 'include',
    });
    const data = await res.json();
    await new Promise(resolve => setTimeout(resolve, 50));
    const cookieMatch = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    xsrfTokenCache = cookieMatch ? decodeURIComponent(cookieMatch[1]) : data.token;
    return xsrfTokenCache;
  };

  const debugSession = async () => {
    const res = await fetch(`${SERVER_URL}debug/session`, {
      method: 'GET',
      credentials: 'include',
    });
    const data = await res.json();
    console.log("🧪 DEBUG SESSION INFO:", data);
  };

  const fetchWithToken = async (url, method = 'GET', body = null) => {
    const token = await getXSRFToken();
    return fetch(url, {
      method,
      credentials: 'include',
      headers: {
        'X-XSRF-TOKEN': token,
        'Content-Type': 'application/json',
      },
      body: body ? JSON.stringify(body) : undefined,
    });
  };

  const handleAddSymbol = async () => {
    try {
      await fetchWithToken(`${SERVER_URL}db/addsymbol?symbol=${symbol}`, 'POST');
      setSymbol("");
      await loadWatchlist();
      setOpen(false);
    } catch (error) {
      console.error("Error adding symbol:", error);
    }
  };

  const handleDeleteSymbol = async (stockSymbol) => {
    try {
      await fetchWithToken(`${SERVER_URL}db/deletesymbol?symbol=${stockSymbol}`, 'DELETE');
      await loadWatchlist();
    } catch (error) {
      console.error("Error deleting symbol:", error);
    }
  };

  const checkUser = async () => {
    const res = await fetch(`${SERVER_URL}db/check`, {
      method: 'GET',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' }
    });
    if (!res.ok) throw new Error("User check failed");
    return await res.json();
  };

  const addUser = async () => {
    const res = await fetchWithToken(`${SERVER_URL}db/adduser`, 'POST');
    if (!res.ok) throw new Error("User creation failed");
    return await res.json();
  };

  const loadWatchlist = async () => {
    const res = await fetch(`${SERVER_URL}db/getsymbols`, {
      method: 'GET',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' }
    });
    if (!res.ok) throw new Error("Watchlist fetch failed");
    const data = await res.json();
    setWatchlist(data);
  };

  const setNewsKey = async (stockSymbol, key) => {
    const res = await fetchWithToken(`${SERVER_URL}db/setnewskey?symbol=${stockSymbol}&key=${key}`, 'POST');
    if (!res.ok) throw new Error("Set news key failed");
    return await res.text();
  };

  const fetchNewsKey = async (stockSymbol) => {
    const res = await fetchWithToken(`${SERVER_URL}db/getnewskey?symbol=${stockSymbol}`, 'GET');
    if (!res.ok) throw new Error("Get news key failed");
    return await res.text();
  };

  const fetchNewsData = async (symbol, key) => {
    const res = await fetchWithToken(`${SERVER_URL}s3/retrieve?key=stock_news/${symbol}/${key}.json`, 'GET');
    if (!res.ok) throw new Error("Fetch news data failed");
    return await res.text();
  };

  const newGenerateNews = async (stockSymbol) => {
    setSelectedStock(stockSymbol);
    const res = await fetchWithToken(`${SERVER_URL}news/generate?symbol=${stockSymbol}`, 'POST');
    if (!res.ok) throw new Error("Generate news failed");
    const key = await res.text();
    const savedKey = await setNewsKey(stockSymbol, key);
    return await fetchNewsData(stockSymbol, savedKey);
  };

  const handleStockDataOpen = async (stockSymbol) => {
    try {
      setSelectedStock(stockSymbol);
      let key = await fetchNewsKey(stockSymbol);
      let newsText = await fetchNewsData(stockSymbol, key);
      if (!newsText || newsText.trim() === "") {
        newsText = await newGenerateNews(stockSymbol);
      }
      const parsed = JSON.parse(newsText);
      const formatted = parsed[0].map((_, i) => ({
        title: parsed[0][i],
        publisher: parsed[1][i],
        url: parsed[2][i],
        score: parsed[3][i],
      }));
      setNewsItems(formatted);
    } catch (error) {
      console.error("Error loading news:", error);
      setNewsItems([]);
    }
  };

  useEffect(() => {
    (async () => {
      await debugSession();
      await getXSRFToken(); // triggers and caches token
      await debugSession();
      const userExists = await checkUser();
      if (!userExists) await addUser();
      await loadWatchlist();
    })();
  }, []);

  return (
    <div id="page-wrapper" className="text-zinc-800 flex h-screen w-full items-center justify-between">
      <div id="watchlist-wrapper" className="rounded-lg ml-16 h-4/5 w-1/4 bg-white flex flex-col p-4">
        <div id="watchlist-header" className="flex items-center justify-between mb-4">
          <h1 className="font-bold text-xl">Watchlist</h1>
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button type="submit" className="ml-auto px-4">+</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px] bg-zinc-900 opacity-100">
              <DialogHeader>
                <DialogTitle>Add a Stock Symbol</DialogTitle>
                <DialogDescription>
                  Type the 4-character stock symbol you would like to add, then press the 'Add' button.
                </DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="name" className="text-right">Symbol</Label>
                  <Input id="name" value={symbol} onChange={(e) => setSymbol(e.target.value)} placeholder="AMZN, TSLA . . ." className="col-span-3" />
                </div>
              </div>
              <DialogFooter>
                <Button className='bg-white text-black font-bold' type="submit" onClick={handleAddSymbol}>Add</Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>

        <div id="watchlist-components" className="mt-4">
          {watchlist.map((stock, index) => (
            <div key={index} className="flex items-center justify-between mb-1">
              <h1 className="font-bold text-lg">
                <Button onClick={() => handleStockDataOpen(stock)} variant="ghost">{stock}</Button>
              </h1>
              <Button className='w-5 h-5 mr-[10px] bg-black' type='submit' size="icon" onClick={() => handleDeleteSymbol(stock.toString())}>
                <MinusIcon className='text-white' />
              </Button>
            </div>
          ))}
        </div>
      </div>

      <div id="stock-data-wrapper" className="rounded-lg mr-16 h-[90%] w-[50%] bg-white flex flex-col items-start justify-start p-6 overflow-y-auto">
        {selectedStock ? (
          <>
            <h1 className="text-2xl font-bold mb-6">{selectedStock}</h1>
            <div className="w-full">
              <StockChart symbol={selectedStock} />
              <p className="text-lg font-semibold m-2 mt-10 mb-4 ">{selectedStock} News Articles</p>
              {newsItems.map((item, index) => {
                const score = parseFloat(item.score);
                const formattedScore = score.toFixed(2);
                let scoreColorClass = "bg-gray-200 text-black";
                if (score >= 0.7) scoreColorClass = "bg-green-500 text-white";
                else if (score >= 0.3) scoreColorClass = "bg-green-200 text-black";
                else if (score <= -0.7) scoreColorClass = "bg-red-500 text-white";
                else if (score <= -0.3) scoreColorClass = "bg-red-200 text-black";

                return (
                  <div key={index} className="flex flex-col mb-4 p-4 border rounded-lg hover:bg-gray-50">
                    <h3 className="text-md font-semibold mb-1">{item.title}</h3>
                    <div className="flex justify-between items-center text-sm text-gray-600 mb-2">
                      <p>Source: {item.publisher}</p>
                      <div className={`px-2 py-1 rounded-md font-medium ${scoreColorClass}`}>Score: {formattedScore}</div>
                    </div>
                    <a href={item.url} target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline text-sm">Read more</a>
                  </div>
                );
              })}
            </div>
          </>
        ) : (
          <div className="h-full w-full flex items-center justify-center">
            <h1 className="text-2xl text-gray-500">Select a symbol from your watchlist.</h1>
          </div>
        )}
      </div>
    </div>
  );
}
