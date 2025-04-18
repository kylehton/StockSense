# StockSense #

## Overview ##
StockSense is a comprehensive full-stack application designed for stock market enthusiasts to track their favorite stocks, visualize market data, and analyze news sentiment. Built with modern web technologies, StockSense provides a seamless experience for users to stay informed about their investments.

## Documentation ##
Basic Documentation (focusing more on the server-side) can be found below:
[https://docs.google.com/document/d/1Bhy2V64S4IomPgVylwh7cgoP70y-eaUC0-QpGOc1BWU/edit?usp=sharing]

## Deployment ##
The project is currently deployed at: [https://www.stock-sense-client.vercel.app]

## Features ##

User Authentication: Secure sign-up/sign-in with Google OAuth
Stock Watchlist: Personalized watchlist to track favorite stocks
Real-time Stock Data: Current prices, previous closing prices, and other key metrics
Interactive Charts: Visual representation of stock performance over time
News Aggregation: Latest news related to watched stocks
Sentiment Analysis: AI-powered analysis of news sentiment (positive, negative, or neutral)
Responsive Design: Optimized for both desktop and mobile devices


## Client-Side Technologies ##
Framework: Next.js
Deployment: Vercel


## Server-Side Technologies ##
Framework: Spring Boot
Deployment: Railway


## Database and Storage ##
Primary Database: PostgreSQL
Session Management: Redis
Cloud Storage: AWS S3

## APIs Used ##
AlphaVantage API for financial data
Yahoo Finance API for supplementary market information

## Security Implementations ##
Spring Security
CSRF token protection
Secure OAuth 2.0 integration
Redis-based session management

### Architecture ###
StockSense follows a serverless microservice architecture:

Frontend Layer: Next.js application handling UI/UX
API Gateway: Spring Boot REST API endpoints
Service Layer: Business logic implementation
Data Access Layer: Database interactions
External Services: Integration with third-party APIs
Deployments: Scaled from usage to minimize costs and idle uptime

### Thank you for going through my project repo! ###