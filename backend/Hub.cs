using System;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Devices.Bluetooth.GenericAttributeProfile;
using ABI.Windows.Devices.Bluetooth;
using TrainController;
using BluetoothLEDevice = Windows.Devices.Bluetooth.BluetoothLEDevice;

namespace BleScanner;

public class Hub(BluetoothLEDevice device, GattCharacteristic gattCharacteristic)
{
    public string name { get; } = device.Name;
    public ulong bluetoothAddress { get; } = device.BluetoothAddress;

    /**
     * speed between -100 (full speed backwards) and 100 (full speed forwards)
     */
    public async Task SetMotorSpeed(int speed)
    {
        byte[] command =
        [
            0x0A, 0x00, // length
            0x81, // Port output command
            0x00, // Port A (might be 0 or 1 depending on the train)
            0x11, // Start power command
            0x01, // Execution flags
            (byte)speed,
            0x64, // Max power
            0x64, // Acceleration
            0x03 // Profile
        ];
        await gattCharacteristic.WriteValueAsync(command.AsBuffer(), GattWriteOption.WriteWithoutResponse);
    }

    /**
     * light intensity between 0 and 100
     */
    public async Task SetLightIntensity(int intensity)
    {
        byte[] command =
        [
            0x0A, 0x00, // length
            0x81, // Port output command
            0x01, // Port A (might be 0 or 1 depending on the train)
            0x11, // Start power command
            0x01, // Execution flags
            (byte)intensity,
            0x64, // Max power
            0x64, // Acceleration
            0x03 // Profile
        ];
        await gattCharacteristic.WriteValueAsync(command.AsBuffer(), GattWriteOption.WriteWithoutResponse);
    }

    public async Task SetLedColor(int colorCode)
    {
        await SetLedColor((Color)colorCode);
    }
    
    private async Task SetLedColor(Color color)
    {
        await gattCharacteristic.WriteValueAsync(
            new byte[] { 0x07, 0x00, 0x81, 0x32, 0x11, 0x51, 0x00, (byte)color }.AsBuffer(),
            GattWriteOption.WriteWithoutResponse);
    }
}