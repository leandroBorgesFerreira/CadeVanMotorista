package br.com.simplepass.cadevanmotorista.location;

import android.app.Activity;
import android.location.Location;

import br.com.simplepass.cadevanmotorista.domain_realm.Path;

/**
 * Interface Locate Manager.
 */
public interface LocateManager {
    /**
     * Start to send the current position to the server.
     *
     * @param activity activity that called this Class
     */
    void start(Activity activity);

    /**
     * Stops to send the position to the server.
     */
    void stop();

    /**
     * Start pick app or deliver the students. The position is sent with the time to arrive and the direction
     * @param path the current path hat is being covered
     */
    void startDelivering(Path path);

    /**
     * Stops deliveting students
     */
    void stopDelivering();

    /**
     * Get que last location that was received by the LocateManager.
     *
     * @return The most recent location.
     */
    Location getLastLocation();
}
