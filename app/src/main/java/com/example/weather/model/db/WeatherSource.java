package com.example.weather.model.db;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.List;

public class WeatherSource {
    private final WeatherDao weatherDao;

    private List<HistoryWeather> historyWeathers;
    private List<Town> towns;

    private HandlerThread handlerThread;
    private Handler handler;

    public WeatherSource(WeatherDao weatherDao) {
        handlerThread = new HandlerThread("handlerThreadWeatherSource");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        this.weatherDao = weatherDao;
    }


    public List<HistoryWeather> getHistoryWeathers() {
        if (historyWeathers == null) {
            loadHistoryweathers();
        }
        return historyWeathers;
    }

    public List<Town> getTowns() {
        if (towns == null) {
            loadTowns();
        }
        return towns;
    }

    public void getHistoryWeathersSortTown() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                historyWeathers = weatherDao.getHistoryWeatherSortTown();
            }
        });
    }

    public void loadHistoryweathers() {
        historyWeathers = weatherDao.getAllHistorWeather();
    }

    public void loadTowns() {
        towns = weatherDao.getAllTown();
}

    public long getCountHistoryWeather() {
        return weatherDao.getCountHistoryWeather();
    }

    public long getCountTown() {
        final long[] count = {0};
        handler.post(new Runnable() {
            @Override
            public void run() {
                count[0] = weatherDao.getCountTown();
            }
        });
        return count[0];
    }

    /**
     * Функция добавления погоды в историю
     * @param historyWeather
     */
    public void addHistoryWeather(final HistoryWeather historyWeather) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                weatherDao.insertHistoryWeather(historyWeather);
                loadHistoryweathers();
            }
        });
    }

    public void addTown(final Town town) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                weatherDao.insertTown(town);
                loadTowns();
            }
        });
    }

    /**
     *
     * @param historyWeather
     */
    public void updateHistorWeather(final HistoryWeather historyWeather) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                weatherDao.updateHistoryWeather(historyWeather);
                loadHistoryweathers();
            }
        });
    }

    public void updateTown(final Town town) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                weatherDao.updateTown(town);
                loadTowns();
            }
        });
    }
    /**
     *
     * @param id
     */
    public void deleteHistorWeather(final long id) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                weatherDao.deleteHistoryWeatherById(id);
                loadHistoryweathers();
            }
        });
    }

    public void deleteAllHistorWeather() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                weatherDao.deleteAllHistoryWeather();
                loadHistoryweathers();
            }
        });
    }

    public List<HistoryWeather> getHistoryWeatherByTown(final String town) {
        historyWeathers = weatherDao.getHistoryWeatherByTown(town);
        return weatherDao.getHistoryWeatherByTown(town);
    }


    public void deleteTown(final long id) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                weatherDao.deleteTownById(id);
                loadTowns();
            }
        });
    }

    public void deleteAllTown() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                weatherDao.deleteAllTown();
                loadTowns();
            }
        });
    }

    public List<Town> getTownByTown(final String town) {
        towns = weatherDao.getTownByTown(town);
        return weatherDao.getTownByTown(town);

    }

    public void stopHandlerThreadWeatherSource() {
        handlerThread.quitSafely();
    }
}
