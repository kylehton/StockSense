export default function Dashboard() {
    return (
        <div id="page-wrapper" className="text-zinc-800 flex h-screen w-full items-center justify-between">
            <div id="watchlist-wrapper" className="ml-16 h-4/5 w-1/4 bg-white opacity-80 flex">
                <div id='watchlist' className='ml-4 mt-4'>
                    <h1 className='font-bold text-xl'>Watchlist</h1>
                </div>
                <div id='watchlist-components'>

                </div>
            </div>
            <div id="stock-data-wrapper" className="mr-16 h-5/6 w-2/5 bg-gray-200 flex items-center justify-center">
                <h1>Stock Data</h1>
            </div>
        </div>
    );
}
