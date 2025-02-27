'use client';
import './dashboard.css';
import React, { useState, useEffect } from 'react';

export default function Dashboard() {

    const [watchlist, setWatchlist] = useState([]);

    useEffect(() => {
        async function loadWatchlist() {
        console.log("Loading watchlist from DB . . .")

        const response = await fetch('http://localhost:8080/getsymbols?Cookie=9474BFA31B89B291BB4C0F23FFE143AD', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then((response) => {
            console.log("Response Headers:", response.headers);
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

        function getCookie(name) {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) return parts.pop().split(';').shift();
            return null; // Cookie not found
          }
          
          // Log the JSESSIONID to the console
          const jsessionId = getCookie('JSESSIONID');
          console.log("JSESSIONID: ", jsessionId);
        loadWatchlist();
    }, []);

    return (
        <div id="page-wrapper" className="text-zinc-800 flex h-screen w-full items-center justify-between">
            <div id="watchlist-wrapper" className="rounded-lg ml-16 h-4/5 w-1/4 bg-white opacity-70 flex flex-col p-4">
                <div id="watchlist-header" className="flex items-center justify-between">
                    <h1 className="font-bold text-xl">Watchlist</h1>
                    <button 
                        id="add-to-watchlist" 
                        className="ml-2 w-8 h-8 flex items-center justify-center border border-zinc-600 rounded-full text-lg leading-none  font-bold hover:bg-gray-100"
                    >
                        <span className='mb-[2px]'>
                            +
                            </span>
                    </button>
                </div>
                <div id="watchlist-components" className="mt-4">
                    {watchlist.map((stock, index) => {
                            <div key={index} className="flex items-center justify-between">
                                <h1 className="font-bold text-lg">{stock}</h1>
                                <button 
                                    id="remove-from-watchlist" 
                                    className="ml-2 w-8 h-8 flex items-center justify-center border border-zinc-600 rounded-full text-lg leading-none  font-bold hover:bg-gray-100"
                                >
                                    <span className='mb-[2px]'>
                                        -
                                    </span>
                                </button>
                            </div>
                        }
                    )}   
                </div>
            </div>
            <div id="stock-data-wrapper" className="rounded-lg mr-16 h-[90%] w-[50%] bg-white opacity-70 flex items-center justify-center">
                <h1 className='text-2xl'>Select a symbol from your watchlist.</h1>
            </div>
        </div>
    );
}
