import SignInModal from "./customcomponents/SignInModal";

export default function Home() {

  return (
    <div id="page-wrapper" className="flex h-screen w-full">
      <div className="flex flex-col items-center">
        <div id='google-sign-in-wrapper' className='absolute top-4 right-8'>
          <SignInModal />
        </div>
        <div id='intro-text-wrapper' className='mt-36 ml-24'>
          <h1 className="font-bold text-6xl mb-4">StockSense</h1>
          <p className='space-grotesk ml-2 w-1/2 text-lg font-light'>
            Welcome to StockSense! Utilize an accessible platform that 
            keeps you updated with the power of AI! Gain valuable insight, track
            trends, and make informed decisions with analytical confidence.
            Sign up today to get started!
          </p>
        </div>
      </div>
    </div>
  );
}