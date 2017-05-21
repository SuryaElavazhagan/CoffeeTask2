package com.example.surya.coffeework;

import java.util.List;

/**
 * Created by surya on 14/5/17.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route, List<Steps> steps);

}
