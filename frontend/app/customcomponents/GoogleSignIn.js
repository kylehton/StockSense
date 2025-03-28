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
    
    /**
     * Retrieves CSRF token to protect against Cross-Site Request Forgery attacks.
     * 
     * The flow works as follows:
     * 1. A request to /xsrf endpoint returns the CSRF token in the response body
     * 2. This token is included in subsequent requests as the X-XSRF-TOKEN header
     * 3. Spring Security validates that the X-XSRF-TOKEN header matches the expected token
     * 
     */
    async function getXSRFToken() {
      try {
          // Make a fetch call to get the CSRF token from the response body
          const response = await fetch('http://localhost:8080/xsrf', {
              method: 'GET',
              credentials: 'include',
              headers: {
                  'Content-Type': 'application/json',
              }
          });
          
          if (!response.ok) {
              throw new Error(`Failed to fetch XSRF token: ${response.status} ${response.statusText}`);
          }
          
          // Parse the response to get the token from the response body
          const data = await response.json();
          
          if (!data || !data.token) {
              throw new Error('XSRF token not found in response body');
          }
          
          console.debug("XSRF token from response:", data.token);
          return data.token;
      } catch (error) {
          console.error("Error fetching XSRF token:", error);
          throw error;
      }
    }
  
    /**
     * Handles the Google Sign-In credential response
     * Sends the credential to the backend for validation and authentication
     */
    const handleCredentialResponse = async (response) => {
      if (response.credential) {
        // Get CSRF token from the server
        const csrfToken = await getXSRFToken();
        
        //response.credential is the JWT token for the authenticated user
        const payload = JSON.parse(atob((response.credential).split(".")[1]));
        console.log("Creds:",response.credential);

        // Debug logging of the token being sent
        console.debug("Sending CSRF token in X-XSRF-TOKEN header:", csrfToken);

        const res = await fetch(`http://localhost:8080/google/auth?id=${response.credential}`, {
          method: "POST",
          credentials: "include", 
          headers: {
              "X-XSRF-TOKEN": csrfToken,
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