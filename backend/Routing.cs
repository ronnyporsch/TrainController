using BleScanner;
using Microsoft.AspNetCore.Mvc;

namespace TrainController
{
    [Route("api/[controller]")]
    [ApiController]
    public class Routing : ControllerBase
    {
        [HttpGet]
        public ActionResult<IEnumerable<Hub>> GetHubs()
        {
            return HubManager.hubs;
        }
        
        [HttpPost("{bleAddress}/setSpeed")]
        public ActionResult SetSpeed(ulong bleAddress, int speed)
        {
            Hub? hub = HubManager.hubs.FirstOrDefault(h => h.bluetoothAddress == bleAddress);
            if (hub == null)
            {
                Console.WriteLine("Hub not found");
                return NotFound();
            }
            hub.SetMotorSpeed(speed);
            return Ok();
        }
        
        [HttpPost("{bleAddress}/setLightIntensity")]
        public ActionResult SetLightIntensity(ulong bleAddress, int lightIntensity)
        {
            Hub? hub = HubManager.hubs.FirstOrDefault(h => h.bluetoothAddress == bleAddress);
            if (hub == null)
            {
                Console.WriteLine("Hub not found");
                return NotFound();
            }
            hub.SetLightIntensity(lightIntensity);
            return Ok();
        }
        
        [HttpPost("{bleAddress}/setLedColor")]
        public ActionResult SetLedColor(ulong bleAddress, int colorCode)
        {
            Hub? hub = HubManager.hubs.FirstOrDefault(h => h.bluetoothAddress == bleAddress);
            if (hub == null)
            {
                Console.WriteLine("Hub not found");
                return NotFound();
            }
            hub.SetLedColor(colorCode);
            return Ok();
        }
    }
}
