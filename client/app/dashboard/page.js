'use client';
import './dashboard.css';
import React, { useState, useEffect } from 'react';
import { MinusIcon } from 'lucide-react';
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import StockChart from './dashboardcomponents/StockChart';

export default function Dashboard() {
    const [watchlist, setWatchlist] = useState([]);
    const [symbol, setSymbol] = useState("");
    const [open, setOpen] = useState(false);
    const [newsItems, setNewsItems] = useState([]);
    const [selectedStock, setSelectedStock] = useState("");
    const SERVER_URL = process.env.NEXT_PUBLIC_SERVER_URL;

    // Fixed getXSRFToken function for dashboard.js
    const getXSRFToken = async () => {
      try {
        // Make request to fetch CSRF token
        const res = await fetch(`${SERVER_URL}xsrf`, {
          method: 'GET',
          credentials: 'include', // Important for cookies
        });

        if (!res.ok) {
          throw new Error(`Failed to get XSRF token: ${res.status}`);
        }

        // Parse the JSON response properly
        const data = await res.json();
        console.log("Token response:", data);

        // If token exists in response, use it
        if (data && data.token) {
          console.log("✅ XSRF token from JSON:", data.token);
          return data.token;
        }

        // Fallback to cookie - make sure to decode it
        const cookieMatch = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
        if (cookieMatch) {
          const tokenFromCookie = decodeURIComponent(cookieMatch[1]);
          console.log("✅ XSRF token from cookie:", tokenFromCookie);
          return tokenFromCookie;
        }

        throw new Error("No XSRF token found in response or cookies");
      } catch (error) {
        console.error("❌ Failed to retrieve CSRF token:", error);
        throw error;
      }
    };

    // Fixed addUser function
    async function addUser() {
      try {
        // Get the token first
        const token = await getXSRFToken();
        console.log("Using token for adduser:", token);
        
        const response = await fetch(`${SERVER_URL}db/adduser`, {
          method: 'POST',
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': token  // Make sure header name matches Spring config
          }
        });
        
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`HTTP error! Status: ${response.status}, Message: ${errorText}`);
        }
        
        return await response.text();
      } catch (error) {
        console.error("Error in addUser:", error);
        throw error;
      }
    }

    const debugSession = async () => {
        const res = await fetch(`${SERVER_URL}debug/session`, {
            method: 'GET',
            credentials: 'include',
        });
        const data = await res.json();
        console.log("🧪 DEBUG SESSION INFO:", data);
    };

    const handleAddSymbol = async () => {
        const token = await getXSRFToken();
        const response = await fetch(`${SERVER_URL}db/addsymbol?symbol=${symbol}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'X-XSRF-TOKEN': token,
                'Content-Type': 'application/json',
            }
        });
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        setSymbol("");
        await loadWatchlist();
        setOpen(false);
    };

    const handleDeleteSymbol = async (stockSymbol) => {
        const token = await getXSRFToken();
        const response = await fetch(`${SERVER_URL}db/deletesymbol?symbol=${stockSymbol}`, {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'X-XSRF-TOKEN': token,
                'Content-Type': 'application/json',
            }
        });
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        await loadWatchlist();
    };

    async function checkUser() {
        const response = await fetch(`${SERVER_URL}db/check`, {
            method: 'GET',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        return await response.json();
    }

    async function loadWatchlist() {
        const response = await fetch(`${SERVER_URL}db/getsymbols`, {
            method: 'GET',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        const data = await response.json();
        setWatchlist(data);
    }

    const setNewsKey = async (stockSymbol, key) => {
        const token = await getXSRFToken();
        const response = await fetch(`${SERVER_URL}db/setnewskey?symbol=${stockSymbol}&key=${key}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'X-XSRF-TOKEN': token,
                'Content-Type': 'application/json',
            }
        });
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        return await response.text();
    };

    const fetchNewsKey = async (stockSymbol) => {
        const token = await getXSRFToken();
        const response = await fetch(`${SERVER_URL}db/getnewskey?symbol=${stockSymbol}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'X-XSRF-TOKEN': token,
                'Content-Type': 'application/json',
            }
        });
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        return await response.text();
    };

    const fetchNewsData = async (symbol, key) => {
        const token = await getXSRFToken();
        const response = await fetch(`${SERVER_URL}s3/retrieve?key=stock_news/${symbol}/${key}.json`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'X-XSRF-TOKEN': token,
                'Content-Type': 'application/json',
            }
        });
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        return await response.text();
    };

    const newGenerateNews = async (stockSymbol) => {
        const token = await getXSRFToken();
        setSelectedStock(stockSymbol);
        const response = await fetch(`${SERVER_URL}news/generate?symbol=${stockSymbol}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'X-XSRF-TOKEN': token,
                'Content-Type': 'application/json',
            }
        });
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        const keyResponse = await response.text();
        const newsKey = await setNewsKey(stockSymbol, keyResponse);
        const newsText = await fetchNewsData(stockSymbol, newsKey);
        return newsText;
    };

    const handleStockDataOpen = async (stockSymbol) => {
        try {
            setSelectedStock(stockSymbol);
            let retrieveKey = await fetchNewsKey(stockSymbol);
            let newsText = await fetchNewsData(stockSymbol, retrieveKey);
            if (!newsText || newsText.trim() === "") {
                newsText = await newGenerateNews(stockSymbol);
            }
            const splitNews = JSON.parse(newsText);
            const storeItems = splitNews[0].map((_, i) => ({
                title: splitNews[0][i],
                publisher: splitNews[1][i],
                url: splitNews[2][i],
                score: splitNews[3][i]
            }));
            setNewsItems(storeItems);
        } catch (error) {
            console.error("Error loading news data:", error);
            setNewsItems([]);
        }
    };

    useEffect(() => {
        async function initialize() {
          await debugSession(); // See current session
          await getXSRFToken(); // This will set cookie + cache the token
      
          const userExists = await checkUser();
          if (!userExists) await addUser(); // Safe CSRF protected
          await loadWatchlist();
        }

          // Debug current cookies
          console.log("Current cookies:", document.cookie);
          
          // Monitor CSRF token usage
          const originalFetch = window.fetch;
          window.fetch = function(...args) {
            if (args[1] && args[1].headers) {
              const headers = args[1].headers;
              console.log("Request URL:", args[0]);
              console.log("Request headers:", headers);
              if (headers['X-XSRF-TOKEN']) {
                console.log("Using XSRF token:", headers['X-XSRF-TOKEN']);
              }
            }
            return originalFetch.apply(this, args);
          };
      
        initialize();
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
                  <Input type="text" 
                      maxlength="4" 
                      pattern="[A-Z]{4}" 
                      id="name" value={symbol} onChange={(e) => setSymbol(e.target.value.toUpperCase())} 
                      placeholder="AMZN, TSLA . . ." className="col-span-3"
                      required />
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
