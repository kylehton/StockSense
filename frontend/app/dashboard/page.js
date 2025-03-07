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
 

export default function Dashboard() {

    // variables for user's stock watchlist
    const [watchlist, setWatchlist] = useState([]);
    // variables for adding a stock symbol
    const [symbol, setSymbol] = useState("");
    // variables for dialog box
    const [open, setOpen] = useState(false);
    // variables for stock news data
    const [newsItems, setNewsItems] = useState([]);
    const [selectedStock, setSelectedStock] = useState("");


    const handleAddSymbol = async () => {
        console.log("Adding symbol:", symbol);
        const response = await fetch(`http://localhost:8080/add?symbol=${symbol}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.text();
        })
        .catch((error) => console.error("Error adding symbol:", error));
        setSymbol("");
        await loadWatchlist();
        setOpen(false);
    };

    const handleDeleteSymbol = async (stockSymbol) => {
        console.log("Deleting symbol:", stockSymbol);
        const response = await fetch(`http://localhost:8080/delete?symbol=${stockSymbol}`, {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.text();
        })
        .catch((error) => console.error("Error deleting symbol:", error));
        setSymbol("");
        await loadWatchlist();
    }

    async function checkUser() {
        const response = await fetch('http://localhost:8080/check', {
            method: 'GET',
            credentials: 'include',
        })
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const result = await response.json();
        console.log("User exists:", result);
        return await result;
    }

    async function addUser() {
            console.log("User does not exist, creating new user.")
            const response = await fetch('http://localhost:8080/adduser', {
                method: 'POST',
                credentials: 'include',
            });
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            const result = await response.json();
            console.log("Result of add:", result);
    }

    async function loadWatchlist() {
        console.log("Loading watchlist.");
        const response = await fetch('http://localhost:8080/getsymbols', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then((data) => {
            console.log("Watchlist data:", data);
            setWatchlist(data); 
        })
        .catch((error) => console.error("Error loading watchlist:", error));
    }

    const handleStockDataOpen = async (stockSymbol) => {
        console.log("Retrieving stock data for:", stockSymbol);
        setSelectedStock(stockSymbol); // Set the selected stock
        const response = await fetch(`http://localhost:8080/generatenews?symbol=${stockSymbol}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            const newsText = await response.text();

            const splitNews = JSON.parse(newsText);
            console.log("News data:", splitNews);

            const storeItems = [];
            const numItems = splitNews[0].length;
            for (let i = 0; i < numItems; i++) {
                const newItem = {
                    title: splitNews[0][i],
                    publisher: splitNews[1][i],
                    url: splitNews[2][i]
                }
                console.log("New item #", i+1, ":", newItem);
                storeItems.push(newItem);
            }
            setNewsItems(storeItems);
        }

    useEffect(() => {
        async function initialize() {
            const userExists = await checkUser();
            if (!userExists) {
                await addUser();
            }
            await loadWatchlist();
        }
        
        initialize();
    }, []);

    return (
        <div id="page-wrapper" className="text-zinc-800 flex h-screen w-full items-center justify-between">
            <div id="watchlist-wrapper" className="rounded-lg ml-16 h-4/5 w-1/4 bg-white flex flex-col p-4">
                <div id="watchlist-header" className="flex items-center justify-between mb-4">
                    <h1 className="font-bold text-xl">Watchlist</h1> 
                        <Dialog open={open} onOpenChange={setOpen}>
                            <DialogTrigger asChild>
                                <Button type="submit" className="ml-auto px-4">
                                    <span className='mb-[2px]'>+</span>
                                </Button>
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
                                    <Label htmlFor="name" className="text-right">
                                    Symbol
                                    </Label>
                                    <Input id="name" value={symbol} onChange={(e) => setSymbol(e.target.value)}
                                     placeholder="AMZN, TSLA . . ." className="col-span-3" />
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
                        <Button onClick={() => (handleStockDataOpen(stock))} variant="ghost">{stock}</Button>
                        </h1>
                        <Button className='w-5 h-5 mr-[10px] bg-black' type='submit' size="icon"
                            onClick={() => {
                                handleDeleteSymbol(stock.toString());
                            }}
                        >
                            <MinusIcon className='text-white' />
                        </Button>
                        
                    </div>
                ))}
                </div>
            </div>
            <div id="stock-data-wrapper" className="rounded-lg mr-16 h-[90%] w-[50%] bg-white flex flex-col items-start justify-start p-6 overflow-y-auto">
            {selectedStock ? (
                <>
                    <h1 className="text-2xl font-bold mb-6">{selectedStock} News</h1>
                    <div className="w-full">
                        {newsItems.map((item, index) => (
                            <div key={index} className="mb-4 p-4 border rounded-lg hover:bg-gray-50">
                                <h2 className="text-lg font-semibold mb-1">{item.title}</h2>
                                <p className="text-sm text-gray-600 mb-2">Source: {item.publisher}</p>
                                <a href={item.url} target="_blank" rel="noopener noreferrer" 
                                className="text-blue-600 hover:underline text-sm">
                                    Read more
                                </a>
                            </div>
                        ))}
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
