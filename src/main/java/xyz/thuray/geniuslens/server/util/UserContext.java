package xyz.thuray.geniuslens.server.util;

public class UserContext {
 
    private final static ThreadLocal<Long> local = new ThreadLocal<>();
 
    public static void setUserId(Long user){
        local.set(user);
    }
 
    public static Long getUserId(){
        return local.get();
    }
 
    public static void clean(){
        if(local.get() != null){
            local.remove();
        }
    }
}