'use client';
import dotenv from "dotenv";
import { useEffect } from "react";
import { useRouter } from 'next/navigation';  

const GoogleSignIn = () => {
    dotenv.config();
    const clientID = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;
    const router = useRouter();

    
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

    async function getCSRFToken() {
      const csrfToken = await fetch('http://localhost:8080/csrf', {
          method: 'GET',
          credentials: 'include',
          headers: {
              'Content-Type': 'application/json',
          }
      })
      const data = await csrfToken.json();
      return data; // return CSRF token value
  }
  
    const handleCredentialResponse = async (response) => {
      const csrfToken = await getCSRFToken();
      if (response.credential) {
        //response.credential is the JWT token for the authenticated user
        const payload = JSON.parse(atob((response.credential).split(".")[1]));
        console.log("Creds:",response.credential);
        console.log("CSRF:",csrfToken);
        console.log("CSRF Token:", csrfToken['token']);

        const res = await fetch(`http://localhost:8080/google/auth?id=${response.credential}`, {
          method: "POST",
          credentials: "include", 
          headers: {
              "X-XSRF-TOKEN": csrfToken['token'],
              "Content-Type": "application/json",
          },
        });
        if (res.ok) {
          console.log(res.text());
          router.push('/dashboard');
        }
        else {
          alert("Failed to authenticate Google account. Please try again.");
        }

      };
    };
  
    return (
      <div id="signin-container">
        <div id="sign-in-button"></div> {/* Google sign-in button will be rendered here */}
      </div>
    );
};
  
export default GoogleSignIn;