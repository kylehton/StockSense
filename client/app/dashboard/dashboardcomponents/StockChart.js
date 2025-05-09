import React, { useState, useEffect, useRef } from 'react';
import { CandlestickSeries, createChart } from 'lightweight-charts';

const StockChart = ({ symbol }) => {
  const chartContainerRef = useRef();
  const chartRef = useRef();
  const seriesRef = useRef();
  const [data, setData] = useState([]);
  const [marketPrice, setMarketPrice] = useState(0)
  const [prevClosing, setPrevClosing] = useState(0)

  const rapidAPIKey = process.env.NEXT_PUBLIC_RAPIDAPI_KEY;

  const getStockData = async () => {
    console.log("Fetching stock data for symbol:", symbol);
    const response = await fetch(
        `https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-chart?interval=15m&symbol=${symbol}&range=5d&region=US`,
        {
            method: 'GET',
            headers: {
              'X-RapidAPI-Key': rapidAPIKey,
              'X-RapidAPI-Host': 'apidojo-yahoo-finance-v1.p.rapidapi.com',
            },
        }
    );
    const data = await response.json();

    const timestamps = data.chart.result[0].timestamp;
    const prices = data.chart.result[0].indicators.quote[0];
    const currentPrice = data.chart.result[0].meta.regularMarketPrice;
    const prevPrice = data.chart.result[0].meta.chartPreviousClose;

    setMarketPrice(currentPrice);
    setPrevClosing(prevPrice);

    const todaysDate = new Date();
    const dateToCheck = new Date();
    dateToCheck.setDate(todaysDate.getDate() - 1)
    dateToCheck.setHours(16, 45, 0, 0)
    const dateInUnix = Math.floor(dateToCheck/1000);
    console.log(todaysDate)
    console.log(dateToCheck)
    console.log("Yest closing time:", dateInUnix)

    const formatted = timestamps.map((t, i) => ({
        time: t,
        open: prices.open[i],
        high: prices.high[i],
        low: prices.low[i],
        close: prices.close[i],
    }));

    const closingPrice = formatted.find(entry => Number(entry.time) === Number(dateInUnix));

    if (closingPrice) {
      console.log("Open price:", closingPrice.open);
    } else {
      console.error("No entry found for Unix time:", dateInUnix);
    }
    
    setPrevClosing(closingPrice)
    setData(formatted);
  };

  useEffect(() => {
    getStockData();
  }, [symbol]);

  useEffect(() => {
    console.log("Checking chart")
    if (!chartContainerRef.current || data.length === 0) return;

    console.log("Creating chart for symbol:", symbol);
    chartRef.current = createChart(chartContainerRef.current, {
      width: 600,
      height: 300,
      layout: {
        background: { type: 'solid', color: '#1a1b1e' },
        textColor: '#d1d5db',
      },
      grid: {
        vertLines: { color: '#2d2d2d' },
        horzLines: { color: '#2d2d2d' },
      },
      timeScale: {
        timeVisible: true,
        secondsVisible: false,
        borderColor: '#2d2d2d',
      },
      crosshair: {
        mode: 1,
        vertLine: {
          color: '#4f46e5',
          width: 1,
          style: 1,
          labelBackgroundColor: '#4f46e5',
        },
        horzLine: {
          color: '#4f46e5',
          width: 1,
          style: 1,
          labelBackgroundColor: '#4f46e5',
        },
      },
      rightPriceScale: {
        borderColor: '#2d2d2d',
      },
    });

    seriesRef.current = chartRef.current.addSeries(CandlestickSeries, {
      upColor: '#22c55e',
      downColor: '#ef4444',
      borderVisible: false,
      wickUpColor: '#22c55e',
      wickDownColor: '#ef4444',
    });    
    console.log("Setting data for chart:", data);
    seriesRef.current.setData(data);

    return () => {
      chartRef.current.remove(); // Cleanup on unmount
    };
  }, [data]);

  return (
    <div id="stock-chart-data-wrapper" className="flex flex-col bg-[#1a1b1e] rounded-xl p-6 shadow-lg">
        <div id="stock-chart-container" ref={chartContainerRef}
          style={{ position: 'relative', width: '100%' }}
          className="rounded-lg overflow-hidden"
        />
        <div id="stock-data-container" className="flex justify-between items-center mt-4 px-2">
            <div className="flex items-center space-x-4">
                <div className="flex flex-col">
                    <span className="text-gray-400 text-sm">Current Price</span>
                    <span className="text-white text-xl font-semibold">${marketPrice?.toFixed(2)}</span>
                </div>
                <div className="flex flex-col">
                    <span className="text-gray-400 text-sm">Previous Close</span>
                    <span className="text-white text-xl font-semibold">${prevClosing?.close?.toFixed(2)}</span>
                </div>
            </div>
            <div className="flex items-center space-x-2">
                <span className={`text-sm font-medium ${marketPrice > prevClosing?.close ? 'text-green-500' : 'text-red-500'}`}>
                    {marketPrice > prevClosing?.close ? '↑' : '↓'} 
                    {((marketPrice - prevClosing?.close) / prevClosing?.close * 100).toFixed(2)}%
                </span>
            </div>
        </div>
    </div>
  );
};

export default StockChart;
