package dev.lemonclient.addon.modules.info;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import net.minecraft.class_1299;
import net.minecraft.class_1538;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_3417;
import net.minecraft.class_3419;

public class KillEffects extends LemonModule {
   private boolean lightningOnceFlag = false;

   public KillEffects() {
      super(LemonClient.Info, "Kill Effects", "");
   }

   public void onKill(class_1657 player) {
      if (this.isActive()) {
         this.spawnLightning(player);
      }

   }

   private void spawnLightning(class_1657 player) {
      class_2338 blockPos = EntityInfo.getBlockPos(player);
      double x = (double)blockPos.method_10263();
      double y = (double)blockPos.method_10264();
      double z = (double)blockPos.method_10260();
      class_1538 lightningEntity = new class_1538(class_1299.field_6112, this.mc.field_1687);
      lightningEntity.method_30634(x, y, z);
      lightningEntity.method_24203(x, y, z);
      this.mc.field_1687.method_2942(lightningEntity.method_5628(), lightningEntity);
      if (!this.lightningOnceFlag) {
         this.mc.field_1687.method_43128(this.mc.field_1724, x, y, z, class_3417.field_14865, class_3419.field_15252, 10000.0F, 0.16000001F);
         this.mc.field_1687.method_43128(this.mc.field_1724, x, y, z, class_3417.field_14956, class_3419.field_15252, 2.0F, 0.1F);
         this.lightningOnceFlag = true;
      }

   }
}
