import './dashboard.css';

export default function Dashboard() {
    return (
        <div id="page-wrapper" className="text-zinc-800 flex h-screen w-full items-center justify-between">
            <div id="watchlist-wrapper" className="ml-16 h-4/5 w-1/4 bg-white opacity-70 flex flex-col p-4">
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
                    {/* Add watchlist items here */}
                </div>
            </div>
            <div id="stock-data-wrapper" className="mr-16 h-[90%] w-[50%] bg-white opacity-70 flex items-center justify-center">
                <h1>Stock Data</h1>
            </div>
        </div>
    );
}
