Documentation for building

authenticate google account and store id in session: POST -> /google/auth?idToken=" token credential here "






access news generation tool (w sentiment analysis): GET -> /getnews?symbol=" enter stock symbol here "





FOR FUNCTIONS INVOLVING SYMBOLS, INCLUDE SET-COOKIE: JSESSIONID (should be auto included if allow credentials)

retrieve all symbols: GET -> /getsymbols

add a symbol: POST -> /add?symbol=" enter stock symbol here "

delete a symbol DELETE -> /delete?symbol=" enter stock symbol here "