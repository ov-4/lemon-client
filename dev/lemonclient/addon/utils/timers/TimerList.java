package dev.lemonclient.addon.utils.timers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TimerList {
   public final List timers = new ArrayList();

   public void add(Object value, double time) {
      this.timers.add(new Timer(value, time));
   }

   public void update() {
      this.timers.removeIf((item) -> {
         return System.currentTimeMillis() > item.endTime;
      });
   }

   public void clear() {
      this.timers.clear();
   }

   public Map getMap() {
      Map map = new HashMap();
      Iterator var2 = this.timers.iterator();

      while(var2.hasNext()) {
         Timer timer = (Timer)var2.next();
         map.put(timer.value, timer.time);
      }

      return map;
   }

   public List getList() {
      List l = new ArrayList();
      Iterator var2 = this.timers.iterator();

      while(var2.hasNext()) {
         Timer timer = (Timer)var2.next();
         l.add(timer.value);
      }

      return l;
   }

   public Object remove(Predicate predicate) {
      Iterator var2 = this.timers.iterator();

      Timer timer;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         timer = (Timer)var2.next();
      } while(!predicate.test(timer));

      this.timers.remove(timer);
      return timer.value;
   }

   public boolean contains(Object value) {
      Iterator var2 = this.timers.iterator();

      Timer timer;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         timer = (Timer)var2.next();
      } while(!timer.value.equals(value));

      return true;
   }

   public static class Timer {
      public final Object value;
      public final long endTime;
      public final double time;

      public Timer(Object value, double time) {
         this.value = value;
         this.endTime = System.currentTimeMillis() + Math.round(time * 1000.0);
         this.time = time;
      }
   }
}
