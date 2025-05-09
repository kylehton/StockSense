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
    <div id="page-wrapper" className="text-zinc-200 min-h-screen max-h-screen overflow-hidden w-full bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 p-4 md:p-8 relative">
      {/* Background decorative elements */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-indigo-500/5 rounded-full blur-3xl"></div>
        <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-purple-500/5 rounded-full blur-3xl"></div>
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-blue-500/5 rounded-full blur-3xl"></div>
        {/* Grid pattern overlay */}
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#4f4f4f2e_1px,transparent_1px),linear-gradient(to_bottom,#4f4f4f2e_1px,transparent_1px)] bg-[size:14px_24px]"></div>
      </div>

      <div className="relative z-10 flex items-center justify-center min-h-screen">
        <div id="watchlist-wrapper" className="rounded-2xl h-[90%] w-full md:w-[600px] bg-gray-800/90 flex flex-col p-4 md:p-6 shadow-2xl hover:shadow-indigo-500/10 transition-all duration-300 border border-gray-700/50">
          {/* Decorative top bar */}
          <div className="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-indigo-500 via-purple-500 to-indigo-500 rounded-t-2xl"></div>
          
          <div id="watchlist-header" className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="w-2 h-8 bg-gradient-to-b from-indigo-500 to-purple-500 rounded-full"></div>
              <h1 className="font-bold text-2xl bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">Watchlist</h1>
            </div>
            <Dialog open={open} onOpenChange={setOpen}>
              <DialogTrigger asChild>
                <Button type="submit" className="ml-auto px-4 bg-indigo-600 hover:bg-indigo-700 transition-all duration-200 shadow-md">+</Button>
              </DialogTrigger>
              <DialogContent className="w-full max-w-[90vw] sm:max-w-[425px] bg-gray-800 border border-gray-700/50 shadow-2xl">
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
                      className="col-span-3 bg-gray-700/50 border-gray-600 text-gray-100 focus:ring-2 focus:ring-indigo-500 transition-all duration-200"
                      required 
                    />
                  </div>
                </div>
                <DialogFooter>
                  <Button className='bg-indigo-600 hover:bg-indigo-700 text-white font-semibold transition-all duration-200 shadow-md' type="submit" onClick={handleAddSymbol}>Add</Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </div>

          {/* Empty state design */}
          {watchlist.length === 0 && (
            <div className="flex-1 flex flex-col items-center justify-center text-center p-8">
              <div className="w-16 h-16 mb-4 rounded-full bg-indigo-500/10 flex items-center justify-center">
                <svg className="w-8 h-8 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                </svg>
              </div>
              <h2 className="text-xl font-semibold text-gray-300 mb-2">Your Watchlist is Empty</h2>
              <p className="text-gray-400 max-w-sm">Add your first stock symbol to start tracking market news and sentiment analysis.</p>
            </div>
          )}

          <div id="watchlist-components" className="mt-4 space-y-2 overflow-y-auto pr-2">
            {watchlist.map((stock, index) => (
              <div key={index} className="flex items-center justify-between p-3 rounded-xl hover:bg-gray-700/50 transition-all duration-200 group border border-transparent hover:border-gray-600/50">
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
                  className='w-8 h-8 bg-red-500 hover:bg-red-600 transition-all duration-200 shadow-md' 
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
          <DialogContent className="max-w-[90vw] max-h-[90vh] bg-gray-800 border border-gray-700/50 shadow-2xl overflow-y-auto">
            {selectedStock && (
              <>
                <div className="flex justify-between items-center mb-6">
                  <div className="flex items-center gap-3">
                    <div className="w-2 h-8 bg-gradient-to-b from-indigo-500 to-purple-500 rounded-full"></div>
                    <h1 className="text-3xl font-bold bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">{selectedStock}</h1>
                  </div>
                </div>
                <div className="w-full">
                  <div className="h-[150px] sm:h-[250px] md:h-[350px] mb-8 relative">
                    <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-indigo-500 via-purple-500 to-indigo-500"></div>
                    <StockChart symbol={selectedStock} />
                  </div>
                  <p className="text-xl font-semibold m-2 mt-12 mb-6 text-gray-200 flex items-center gap-2">
                    <span className="w-1.5 h-6 bg-gradient-to-b from-indigo-500 to-purple-500 rounded-full"></span>
                    {selectedStock} News Articles
                  </p>
                  {newsItems.map((item, index) => {
                    const score = parseFloat(item.score);
                    const formattedScore = score.toFixed(2);
                    let scoreColorClass = "bg-gray-700/50 text-gray-300";
                    let scoreGradient = "";
                    if (score >= 0.7) {
                      scoreColorClass = "bg-green-600/90 text-white";
                      scoreGradient = "from-green-500 to-emerald-500";
                    } else if (score >= 0.3) {
                      scoreColorClass = "bg-green-900/90 text-green-200";
                      scoreGradient = "from-green-600 to-emerald-600";
                    } else if (score <= -0.7) {
                      scoreColorClass = "bg-red-600/90 text-white";
                      scoreGradient = "from-red-500 to-rose-500";
                    } else if (score <= -0.3) {
                      scoreColorClass = "bg-red-900/90 text-red-200";
                      scoreGradient = "from-red-600 to-rose-600";
                    }

                    return (
                      <div key={index} className="flex flex-col mb-6 p-6 border border-gray-700/50 rounded-xl hover:bg-gray-700/50 transition-all duration-200 group">
                        <div className="flex items-start justify-between gap-4">
                          <h3 className="text-lg font-semibold mb-2 text-gray-100 group-hover:text-indigo-400 transition-colors duration-200 flex-1">{item.title}</h3>
                          <div className={`px-3 py-1.5 rounded-full font-medium ${scoreColorClass} transition-colors duration-200 shadow-md flex items-center gap-1.5 min-w-[100px] justify-center`}>
                            <div className={`w-2 h-2 rounded-full bg-gradient-to-r ${scoreGradient}`}></div>
                            {formattedScore}
                          </div>
                        </div>
                        <div className="flex justify-between items-center text-sm text-gray-400 mb-3">
                          <p className="font-medium flex items-center gap-2">
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z" />
                            </svg>
                            {item.publisher}
                          </p>
                        </div>
                        <a 
                          href={item.url} 
                          target="_blank" 
                          rel="noopener noreferrer" 
                          className="text-indigo-400 hover:text-indigo-300 text-sm font-medium hover:underline transition-colors duration-200 inline-flex items-center group/link"
                        >
                          Read more 
                          <svg className="w-4 h-4 ml-1 transform group-hover/link:translate-x-1 transition-transform duration-200" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
    </div>
  );
}
