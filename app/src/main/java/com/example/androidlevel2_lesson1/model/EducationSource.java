package com.example.androidlevel2_lesson1.model;

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
        historyWeathers = educationDao.getHistoryWeatherSortTown();
    }

    public void getHistoryWeathersNotSort() {
        historyWeathers = educationDao.getHistoryWeatherNotSort();
    }

    public void loadHistoryweathers() {
        historyWeathers = educationDao.getAllHistorWeather();
    }

    public void loadTowns() {
        towns = educationDao.getAllTown();
}

    public long getCountHistoryWeather() {
        return educationDao.getCountHistoryWeather();
    }

    public long getCountTown() {

        return educationDao.getCountTown();
    }

    /**
     * Функция добавления погоды в историю
     * @param historyWeather
     */
    public void addHistoryWeather(HistoryWeather historyWeather) {
        educationDao.insertHistoryWeather(historyWeather);
        loadHistoryweathers();
    }

    public void addTown(Town town) {
        educationDao.insertTown(town);
        loadTowns();
    }

    /**
     *
     * @param historyWeather
     */
    public void updateHistorWeather(HistoryWeather historyWeather) {
        educationDao.updateHistoryWeather(historyWeather);
        loadHistoryweathers();
    }

    public void updateTown(Town town) {
        educationDao.updateTown(town);
        loadTowns();
    }
    /**
     *
     * @param id
     */
    public void deleteHistorWeather(long id) {
        educationDao.deleteHistoryWeatherById(id);
        loadHistoryweathers();
    }

    public void deleteAllHistorWeather() {
        educationDao.deleteAllHistoryWeather();
        loadHistoryweathers();
    }

    public void getHistoryWeatherByTown(String town) {
        historyWeathers = educationDao.getHistoryWeatherByTown(town);
    }

    public void deleteTown(long id) {
        educationDao.deleteTownById(id);
        loadTowns();
    }

    public void deleteAllTown() {
        educationDao.deleteAllTown();
        loadTowns();
    }

    public void getTownByTown(String town) {
        towns = educationDao.getTownByTown(town);
    }

}
