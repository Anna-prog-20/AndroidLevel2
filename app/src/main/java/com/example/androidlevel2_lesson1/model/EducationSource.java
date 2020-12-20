package com.example.androidlevel2_lesson1.model;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.Delete;

import java.util.List;

public class EducationSource {
    private final EducationDao educationDao;

    private List<HistoryWeather> historyWeathers;
    private List<Town> towns;

    public EducationSource(EducationDao educationDao) {
        this.educationDao = educationDao;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                historyWeathers = educationDao.getHistoryWeatherSortTown();
                Log.i("TAG", Thread.currentThread().getName());
            }
        }).start();
    }

    public void loadHistoryweathers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                historyWeathers = educationDao.getAllHistorWeather();
                Log.i("TAG", Thread.currentThread().getName());
            }
        }).start();
    }

    public void loadTowns() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                towns = educationDao.getAllTown();
                Log.i("TAG", Thread.currentThread().getName());
            }
        }).start();

}

    public long getCountHistoryWeather() {
        return educationDao.getCountHistoryWeather();
    }

    public long getCountTown() {
        final long[] count = {0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                count[0] = educationDao.getCountTown();
            }
        }).start();
        return count[0];
    }

    /**
     * Функция добавления погоды в историю
     * @param historyWeather
     */
    public void addHistoryWeather(final HistoryWeather historyWeather) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                educationDao.insertHistoryWeather(historyWeather);
                loadHistoryweathers();
            }
        }).start();

    }

    public void addTown(final Town town) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                educationDao.insertTown(town);
                loadTowns();
            }
        });
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param historyWeather
     */
    public void updateHistorWeather(final HistoryWeather historyWeather) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                educationDao.updateHistoryWeather(historyWeather);
                loadHistoryweathers();
            }
        }).start();
    }

    public void updateTown(final Town town) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                educationDao.updateTown(town);
                loadTowns();
            }
        }).start();
    }
    /**
     *
     * @param id
     */
    public void deleteHistorWeather(final long id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                educationDao.deleteHistoryWeatherById(id);
                loadHistoryweathers();
            }
        }).start();
    }

    public void deleteAllHistorWeather() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                educationDao.deleteAllHistoryWeather();
                loadHistoryweathers();
            }
        }).start();
    }

    public void getHistoryWeatherByTown(final String town) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                historyWeathers = educationDao.getHistoryWeatherByTown(town);
            }
        });
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteTown(final long id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                educationDao.deleteTownById(id);
                loadTowns();
            }
        }).start();
    }

    public void deleteAllTown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                educationDao.deleteAllTown();
                loadTowns();
            }
        }).start();
    }

    public void getTownByTown(final String town) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                towns = educationDao.getTownByTown(town);
            }
        });
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
