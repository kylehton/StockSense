import React, { useState, useEffect, useRef } from 'react';
import { CandlestickSeries, createChart } from 'lightweight-charts';

const StockChart = ({ symbol }) => {
  const chartContainerRef = useRef();
  const chartRef = useRef();
  const seriesRef = useRef();
  const [data, setData] = useState([]);

  const rapidAPIKey = process.env.NEXT_PUBLIC_RAPIDAPI_KEY;

  const getStockData = async () => {
    console.log("Fetching stock data for symbol:", symbol);
    const response = await fetch(
      `https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-chart?interval=1d&symbol=${symbol}&range=1mo&region=US`,
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

    const formatted = timestamps.map((t, i) => ({
        time: new Date(t * 1000).toISOString().split('T')[0],
        open: prices.open[i],
        high: prices.high[i],
        low: prices.low[i],
        close: prices.close[i],
    }));

    setData(formatted);
  };

  useEffect(() => {
    getStockData();
  }, [symbol]);

  useEffect(() => {
    console.log("Checking chart")
    if (!chartContainerRef.current || data.length === 0) return;

    //if (chartRef.current) chartRef.current.remove(); // clean up old chart

    console.log("Creating chart for symbol:", symbol);
    chartRef.current = createChart(chartContainerRef.current, {
      width: 350,
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

    seriesRef.current = chartRef.current.addSeries(CandlestickSeries);
    console.log("Setting data for chart:", data);
    seriesRef.current.setData(data);

    return () => {
      chartRef.current.remove(); // Cleanup on unmount
    };
  }, [data]);

  return (
    <div
      ref={chartContainerRef}
      style={{ position: 'relative', width: '800px', height: '400px' }}
    />
  );
};

export default StockChart;
