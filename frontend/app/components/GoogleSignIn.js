'use client';
import dotenv from "dotenv";
import { useEffect } from "react";

const GoogleSignIn = () => {
    dotenv.config();
    const clientID = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;

    // unable to read from .env file, so manually insert the clientID when using localhost, and 
    // revert back to this when pushing onto main branch for deployment
  
    useEffect(() => {
      const initGoogleSignIn = () => {
        if (window.google) {
          window.google.accounts.id.initialize({
            client_id: clientID,
            callback: handleCredentialResponse,
          });
  
          // Render the full-size Google Sign-In button
          window.google.accounts.id.renderButton(
            document.getElementById("sign-in-button"), 
            {
              theme: "outline", 
              size: "large", 
              type: "standard" 
            }
          );
        }
      };
  
      // Load the Google Sign-In script and initialize Google Sign-In
      const script = document.createElement('script');
      script.src = 'https://accounts.google.com/gsi/client';
      script.async = true;
      script.defer = true;
      document.body.appendChild(script);
  
      script.onload = initGoogleSignIn;
  
      return () => {
        document.body.removeChild(script);
      };
    }, [clientID]);
  
    const handleCredentialResponse = async (response) => {
      if (response.credential) {
        //response.credential is the JWT token for the authenticated user
        const payload = JSON.parse(atob((response.credential).split(".")[1]));
        console.log("Token credentials:", response.credential);
        console.log("User ID:", payload["sub"]);// accesses the user ID from token
        
      };
    };
  
    return (
      <div id="signin-container">
        <div id="sign-in-button"></div> {/* Google sign-in button will be rendered here */}
      </div>
    );
};
  
export default GoogleSignIn;