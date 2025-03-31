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
    
<<<<<<< HEAD
    /**
     * Retrieves CSRF token to protect against Cross-Site Request Forgery attacks.
     * 
     * The flow works as follows:
     * 1. A request to /xsrf endpoint returns the CSRF token in the response body
     * 2. This token is included in subsequent requests as the X-XSRF-TOKEN header
     * 3. Spring Security validates that the X-XSRF-TOKEN header matches the expected token
     * 
     */
=======
>>>>>>> parent of 29f2e0b (working csrf authentication for all endpoints)
    async function getXSRFToken() {
      try {
          const xsrfToken = await fetch('http://localhost:8080/xsrf', {
              method: 'GET',
              credentials: 'include',
              headers: {
                  'Content-Type': 'application/json',
              }
          });
          //document.cookie = `XSRF-TOKEN=${xsrfToken.headers.get('x-xsrf-token')}; path=/`; // Store it in cookies
          
          return xsrfToken.headers.get('x-xsrf-token');
      } catch (error) {
          console.error("Error fetching XSRF token:", error);
          throw error;
      }
    }
  
    const handleCredentialResponse = async (response) => {
      if (response.credential) {
        const xsrf = await getXSRFToken();
        //response.credential is the JWT token for the authenticated user
        const payload = JSON.parse(atob((response.credential).split(".")[1]));
        console.log("Creds:",response.credential);
<<<<<<< HEAD
=======
        console.log("XSRF from backend:", xsrf);
        console.log("Session ID:", getCookie("JSESSIONID"))
        console.log("Session ID from backend:", await getSessionID());
>>>>>>> parent of 29f2e0b (working csrf authentication for all endpoints)

        const res = await fetch(`http://localhost:8080/google/auth?id=${response.credential}`, {
          method: "POST",
          credentials: "include", 
          headers: {
              "X-Xsrf-Token": `${xsrf}`,
              "Content-Type": "application/json",
          },
        })
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