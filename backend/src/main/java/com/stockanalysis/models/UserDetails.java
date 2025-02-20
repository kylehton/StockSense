package com.stockanalysis.models;

public class UserDetails {
    // ThreadLocal to store user ID for the current thread, keeping sessions separate
    // static so that it is shared across all instances of UserDetails
    private static final ThreadLocal<String> currentUserId = new ThreadLocal<>();

    public void setUserId(String userId) 
    {
        currentUserId.set(userId);
    }

    public String getUserId() 
    {
        return currentUserId.get();
    }

    public void clear() 
    {
        currentUserId.remove();
    }
}
