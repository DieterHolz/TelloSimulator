package tellosimulator.state;

public class TelloStateSerializer {
    /**
     * Builds the String representation of the state sent by the drone
     *
     * @param state
     * @return String representation of a TelloDroneState
     */
    public static String serializeState(TelloDroneState state) {
        StringBuilder serializedState = new StringBuilder();
        //from SDK: Data string received when the mission pad detection feature is disabled:
        //“pitch:%d;roll:%d;yaw:%d;vgx:%d;vgy%d;vgz:%d;templ:%d;temph:%d;tof:%d;h:%d;bat:%d;baro:% .2f; time:%d;agx:%.2f;agy:%.2f;agz:%.2f;\r\n”
        serializedState.append("pitch:" + state.getPitch() + ";");
        serializedState.append("roll:" +  state.getRoll() + ";");
        serializedState.append("yaw:" +  state.getYaw() + ";");
        serializedState.append("vgx:" +  state.getSpeedX() + ";");
        serializedState.append("vgy:" +  state.getSpeedY() + ";");
        serializedState.append("vgz:" +  state.getSpeedZ() + ";");
        serializedState.append("templ:" +  state.getTempLow() + ";");
        serializedState.append("temph:" +  state.getTempHigh() + ";");
        serializedState.append("tof:" +  state.getTofDistance() + ";");
        serializedState.append("h:" +  state.getHeight() + ";");
        serializedState.append("bat:" +  state.getBattery() + ";");
        serializedState.append("baro:" +  state.getBarometer() + ";");
        serializedState.append("time:" +  state.getMotorTime() + ";");
        serializedState.append("agx:" +  state.getAccelerationX() + ";");
        serializedState.append("agy:" +  state.getAccelerationY() + ";");
        serializedState.append("agz:" +  state.getAccelerationZ() + ";");
        serializedState.append(".2f;\\r\\n");

        return serializedState.toString();
    }
}
