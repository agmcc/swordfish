package rocket;

import com.github.agmcc.swordfish.annotation.Module;

@Module(packages = {"rocket", "rocket.propulsion"})
public interface RocketModule {

  Rocket rocket();
}
