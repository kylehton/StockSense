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
        backgroundColor: '#000000',
        textColor: '#ffffff',
      },
      grid: {
        vertLines: { color: '#444' },
        horzLines: { color: '#444' },
      },
      timeScale: {
        timeVisible: true,
        secondsVisible: false,
      },
      crosshair: {
        mode: 1,
      },
    });
    chartRef.current.timeScale().applyOptions({
      fixLeftEdge: true,
      fixRightEdge: true,
      barSpacing: 5,
      timeVisible: true,
      rightOffset: 0
    });

    seriesRef.current = chartRef.current.addSeries(CandlestickSeries);
    console.log("Setting data for chart:", data);
    seriesRef.current.setData(data);

    return () => {
      chartRef.current.remove(); // Cleanup on unmount
    };
  }, [data]);

  return (
    <div id="stock-chart-data-wrapper" className="flex flex-col">
        <div id="stock-chart-container" ref={chartContainerRef}
          style={{ position: 'relative', width: '600px' }}
        />
        <div id="stock-data-container" className="flex flex-col ml-2">
            <p className="mb-2">Price: {marketPrice}</p>
            <p className="mb-2">Prev. Closing: {prevClosing}</p>
        </div>
    </div>
  );
};

export default StockChart;
