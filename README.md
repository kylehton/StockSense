Documentation for building

access webscraping tool: GET -> /scrape?url=" enter url link here "

access news generation tool: GET -> /getnews?symbol=" enter stock symbol here "

get s3 bucket data: /s3/retrieve?id=" enter item object key here (without prefix and type) "

authenticate google account and store id in session: /google/auth?idToken=" token credential here "

get sentiment: /analyze-sentiment?key=" enter key to item in s3 bucket "


FOR FUNCTIONS INVOLVING SYMBOLS, INCLUDE SET-COOKIE: JSESSIONID (should be auto included if allow credentials)
retrieve all symbols: GET -> /getsymbols

add a symbol: GET -> /add?symbol=" enter stock symbol here "

delete a symbol GET -> /delete?symbol=" enter stock symbol here "