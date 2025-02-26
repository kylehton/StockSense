'use client';
import './dashboard.css';
import React, { useState, useEffect } from 'react';

export default function Dashboard() {

    const [watchlist, setWatchlist] = useState([]);

    

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
