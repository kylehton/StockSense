import SignInModal from "./customcomponents/SignInModal";

export default function Home() {
  return (
    <div id="page-wrapper" className="flex h-screen w-full bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 relative overflow-hidden">
      {/* Background stock chart pattern */}
      <div className="absolute inset-0 opacity-10">
        <svg className="w-full h-full" viewBox="0 0 100 100" preserveAspectRatio="none">
          <path
            d="M0,50 Q10,40 20,45 T40,35 T60,55 T80,45 T100,50"
            fill="none"
            stroke="white"
            strokeWidth="0.5"
          />
          <path
            d="M0,60 Q10,50 20,55 T40,45 T60,65 T80,55 T100,60"
            fill="none"
            stroke="white"
            strokeWidth="0.5"
          />
        </svg>
      </div>

      <div className="flex flex-col items-center w-full relative z-10">
        <div id='google-sign-in-wrapper' className='absolute top-8 right-8'>
          <SignInModal />
        </div>
        
        <div id='intro-text-wrapper' className='mt-32 max-w-4xl px-8 text-center'>
          <h1 className="font-bold text-7xl mb-6 bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">
            StockSense
          </h1>
          <p className='space-grotesk text-xl text-gray-300 leading-relaxed mb-12'>
            Welcome to StockSense! Utilize an accessible platform that 
            keeps you updated with the power of AI! Gain valuable insight, track
            trends, and make informed decisions with analytical confidence.
            Sign up today to get started!
          </p>
          
          {/* Feature highlights */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-12">
            <div className="p-6 rounded-xl bg-gray-800/50 backdrop-blur-sm border border-gray-700 hover:border-indigo-500 transition-all duration-300">
              <h3 className="text-xl font-semibold text-indigo-400 mb-3">AI-Powered Analysis</h3>
              <p className="text-gray-400">Get instant insights and sentiment analysis from the latest market news</p>
            </div>
            <div className="p-6 rounded-xl bg-gray-800/50 backdrop-blur-sm border border-gray-700 hover:border-indigo-500 transition-all duration-300">
              <h3 className="text-xl font-semibold text-indigo-400 mb-3">Real-time Tracking</h3>
              <p className="text-gray-400">Monitor your favorite stocks with live updates and performance metrics</p>
            </div>
            <div className="p-6 rounded-xl bg-gray-800/50 backdrop-blur-sm border border-gray-700 hover:border-indigo-500 transition-all duration-300">
              <h3 className="text-xl font-semibold text-indigo-400 mb-3">Smart Watchlist</h3>
              <p className="text-gray-400">Create and manage your personalized stock watchlist with ease</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}