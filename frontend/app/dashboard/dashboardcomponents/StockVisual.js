import React, { useEffect, useState } from 'react';

export default function StockVisual (stockSymbol) {
    const [select, setSelect] = useState(false); // if symbol is selected

    return (
        <div id="stock-data-wrapper" className="rounded-lg mr-16 h-[90%] w-[50%] bg-white flex items-center justify-center">
                <h1 className='text-2xl'>Select a symbol from your watchlist.</h1>
            </div>
    );
}