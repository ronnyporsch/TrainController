using Windows.Devices.Bluetooth;
using Windows.Devices.Bluetooth.Advertisement;
using Windows.Devices.Bluetooth.GenericAttributeProfile;
using BleScanner;

namespace TrainController;

static class HubManager
{
    private const int LEGO_COMPANY_ID = 0x0397;
    public static List<Hub> hubs = new();

    public static async Task ScanForLEGOHubsContinuously()
    {
        var watcher = new BluetoothLEAdvertisementWatcher();
        watcher.ScanningMode = BluetoothLEScanningMode.Active;

        watcher.Received += async (w, eventArgs) =>
        {
            if (eventArgs.Advertisement.ManufacturerData.All(m => m.CompanyId != LEGO_COMPANY_ID)) return;

            Console.WriteLine("LEGO Hub detected, attempting to connect...");

            var device = await BluetoothLEDevice.FromBluetoothAddressAsync(eventArgs.BluetoothAddress);
            if (device == null)
            {
                Console.WriteLine("Device connection failed.");
                return;
            }

            Console.WriteLine($"Connected to: {device.Name}");

            // Get LEGO Service
            Console.WriteLine("getting services...");
            var servicesResult = await device.GetGattServicesAsync();
            Console.WriteLine($"Services found: {servicesResult.Status}");
            var legoService = servicesResult.Services.FirstOrDefault(s =>
                s.Uuid == Guid.Parse("00001623-1212-efde-1623-785feabcd123"));

            if (legoService == null)
            {
                Console.WriteLine("LEGO Service not found.");
                return;
            }

            // Get write characteristic
            Console.WriteLine("getting characteristics...");
            var characteristicsResult = await legoService.GetCharacteristicsAsync();
            Console.WriteLine($"Characteristics found: {characteristicsResult.Characteristics.Count}");
            foreach (var characteristicsResultCharacteristic in characteristicsResult.Characteristics)
            {
                Console.WriteLine(characteristicsResultCharacteristic);
            }

            var writeCharacteristic = characteristicsResult.Characteristics.FirstOrDefault(c =>
                c.Uuid == Guid.Parse("00001624-1212-efde-1623-785feabcd123") &&
                c.CharacteristicProperties.HasFlag(GattCharacteristicProperties.WriteWithoutResponse));

            if (writeCharacteristic == null)
            {
                Console.WriteLine("Write characteristic not found.");
                return;
            }
            hubs.Add(new Hub(device, writeCharacteristic));
        };
        watcher.Start();
        Console.WriteLine("Scanning for LEGO Hubs...");
        await Task.Delay(-1); // keep app alive
    }
}