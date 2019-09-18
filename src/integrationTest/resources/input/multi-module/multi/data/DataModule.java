package multi.data;

import com.github.agmcc.swordfish.annotation.Module;

@Module(packages = "multi.data")
public interface DataModule {

  Database database();
}
