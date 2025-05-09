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
    <div id="page-wrapper" className="text-zinc-200 flex h-screen w-full items-center justify-center bg-gradient-to-br from-gray-900 to-gray-800 p-8">
      <div id="watchlist-wrapper" className="rounded-xl h-[90%] w-[600px] bg-gray-800 flex flex-col p-6 shadow-lg hover:shadow-xl transition-all duration-300 border border-gray-700">
        <div id="watchlist-header" className="flex items-center justify-between mb-6">
          <h1 className="font-bold text-2xl bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">Watchlist</h1>
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button type="submit" className="ml-auto px-4 bg-indigo-600 hover:bg-indigo-700 transition-colors duration-200">+</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px] bg-gray-800 border border-gray-700 shadow-2xl">
              <DialogHeader>
                <DialogTitle className="text-xl font-semibold text-gray-100">Add a Stock Symbol</DialogTitle>
                <DialogDescription className="text-gray-400">
                  Type the 4-character stock symbol you would like to add, then press the 'Add' button.
                </DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="name" className="text-right text-gray-300">Symbol</Label>
                  <Input 
                    type="text" 
                    maxLength="4" 
                    pattern="[A-Z]{4}" 
                    id="name" 
                    value={symbol} 
                    onChange={(e) => setSymbol(e.target.value.toUpperCase())} 
                    placeholder="AMZN, TSLA . . ." 
                    className="col-span-3 bg-gray-700 border-gray-600 text-gray-100 focus:ring-2 focus:ring-indigo-500 transition-all duration-200"
                    required 
                  />
                </div>
              </div>
              <DialogFooter>
                <Button className='bg-indigo-600 hover:bg-indigo-700 text-white font-semibold transition-colors duration-200' type="submit" onClick={handleAddSymbol}>Add</Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>

        <div id="watchlist-components" className="mt-4 space-y-2 overflow-y-auto">
          {watchlist.map((stock, index) => (
            <div key={index} className="flex items-center justify-between p-3 rounded-lg hover:bg-gray-700 transition-colors duration-200 group">
              <h1 className="font-semibold text-lg">
                <Button 
                  onClick={() => handleStockDataOpen(stock)} 
                  variant="ghost" 
                  className="hover:text-indigo-400 transition-colors duration-200 text-gray-200"
                >
                  {stock}
                </Button>
              </h1>
              <Button 
                className='w-8 h-8 bg-red-500 hover:bg-red-600 transition-colors duration-200' 
                type='submit' 
                size="icon" 
                onClick={() => handleDeleteSymbol(stock.toString())}
              >
                <MinusIcon className='text-white' />
              </Button>
            </div>
          ))}
        </div>
      </div>

      <Dialog open={!!selectedStock} onOpenChange={() => setSelectedStock("")}>
        <DialogContent className="max-w-[90vw] max-h-[90vh] bg-gray-800 border border-gray-700 shadow-2xl overflow-y-auto">
          {selectedStock && (
            <>
              <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">{selectedStock}</h1>
                <Button 
                  variant="ghost" 
                  className="h-8 w-8 p-0 hover:bg-gray-700" 
                  onClick={() => setSelectedStock("")}
                >
                  <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </Button>
              </div>
              <div className="w-full">
                <StockChart symbol={selectedStock} />
                <p className="text-xl font-semibold m-2 mt-12 mb-6 text-gray-200">{selectedStock} News Articles</p>
                {newsItems.map((item, index) => {
                  const score = parseFloat(item.score);
                  const formattedScore = score.toFixed(2);
                  let scoreColorClass = "bg-gray-700 text-gray-300";
                  if (score >= 0.7) scoreColorClass = "bg-green-600 text-white";
                  else if (score >= 0.3) scoreColorClass = "bg-green-900 text-green-200";
                  else if (score <= -0.7) scoreColorClass = "bg-red-600 text-white";
                  else if (score <= -0.3) scoreColorClass = "bg-red-900 text-red-200";

                  return (
                    <div key={index} className="flex flex-col mb-6 p-6 border border-gray-700 rounded-xl hover:bg-gray-700 transition-all duration-200 group">
                      <h3 className="text-lg font-semibold mb-2 text-gray-100 group-hover:text-indigo-400 transition-colors duration-200">{item.title}</h3>
                      <div className="flex justify-between items-center text-sm text-gray-400 mb-3">
                        <p className="font-medium">Source: {item.publisher}</p>
                        <div className={`px-3 py-1.5 rounded-full font-medium ${scoreColorClass} transition-colors duration-200`}>
                          Score: {formattedScore}
                        </div>
                      </div>
                      <a 
                        href={item.url} 
                        target="_blank" 
                        rel="noopener noreferrer" 
                        className="text-indigo-400 hover:text-indigo-300 text-sm font-medium hover:underline transition-colors duration-200 inline-flex items-center"
                      >
                        Read more 
                        <svg className="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                        </svg>
                      </a>
                    </div>
                  );
                })}
              </div>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
