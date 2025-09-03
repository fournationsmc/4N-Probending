package net.fournationsmc.probending.libraries.database;

public interface Callback<TYPE> {

    public void run(TYPE dataReturning);
}
