import clr #package pythonnet, not clr
import wmi
import time


openhardwaremonitor_hwtypes = ['Mainboard','SuperIO','CPU','RAM','GpuNvidia','GpuAti','TBalancer','Heatmaster','HDD']
cputhermometer_hwtypes = ['Mainboard','SuperIO','CPU','GpuNvidia','GpuAti','TBalancer','Heatmaster','HDD']
openhardwaremonitor_sensortypes = ['Voltage','Clock','Temperature','Load','Fan','Flow','Control','Level','Factor','Power','Data','SmallData']
cputhermometer_sensortypes = ['Voltage','Clock','Temperature','Load','Fan','Flow','Control','Level']


def initialize_openhardwaremonitor():
    file = 'OpenHardwareMonitorLib'
    clr.AddReference(file)

    from OpenHardwareMonitor import Hardware

    handle = Hardware.Computer()
    handle.MainboardEnabled = True
    handle.CPUEnabled = True
    handle.RAMEnabled = True
    handle.GPUEnabled = True
    handle.HDDEnabled = True
    handle.Open()
    return handle

def initialize_cputhermometer():
    file = r'C:\Users\Tednokent01\Downloads\MyCity\traffic_analyzer\CPUThermometerLib'
    clr.FindAssembly(file)
    clr.AddReference(file)
    from CPUThermometer import Hardware

    handle = Hardware.Computer()
    handle.CPUEnabled = True
    handle.Open()
    return handle

def fetch_stats(handle):
    for i in handle.Hardware:
        i.Update()
        for sensor in i.Sensors:
            parse_sensor(sensor)
        for j in i.SubHardware:
            j.Update()
            for subsensor in j.Sensors:
                parse_sensor(subsensor)


def parse_sensor(sensor):
    if sensor.Value is not None:
        if type(sensor).__module__ == 'CPUThermometer.Hardware':
            sensortypes = cputhermometer_sensortypes
            hardwaretypes = cputhermometer_hwtypes
        elif type(sensor).__module__ == 'OpenHardwareMonitor.Hardware':
            sensortypes = openhardwaremonitor_sensortypes
            hardwaretypes = openhardwaremonitor_hwtypes
        else:
            return

        if sensor.SensorType == sensortypes.index('Temperature'):
            print(u"%s %s Temperature Sensor #%i %s - %s\u00B0C" % (hardwaretypes[sensor.Hardware.HardwareType], sensor.Hardware.Name, sensor.Index, sensor.Name, sensor.Value))

if __name__ == "__main__":
    print("OpenHardwareMonitor:")
    start = time.time()
    #HardwareHandle = initialize_openhardwaremonitor()
    #print(time.time() - start)
    #start = time.time()
    #fetch_stats(HardwareHandle)
    print(time.time() - start)
    print("\nCPUMonitor:")
    CPUHandle = initialize_cputhermometer()
    fetch_stats(CPUHandle)