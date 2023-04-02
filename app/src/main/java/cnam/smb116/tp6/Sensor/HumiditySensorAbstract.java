package cnam.smb116.tp6.Sensor;

public abstract class HumiditySensorAbstract{
    // valeur du capteur, précision de 0.1
    public abstract float value() throws Exception;

    // période minimale entre deux lectures
    public abstract long minimalPeriod();
}

