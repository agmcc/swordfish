package multi.api;

import com.github.agmcc.swordfish.annotation.Module;
import multi.data.DataModule;

@Module(packages = "multi.api", uses = DataModule.class)
public interface ApiModule {

  Controller controller();
}
