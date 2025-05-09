using BleScanner;
using Microsoft.AspNetCore.Mvc;

namespace TrainController.Routing
{
    [Route("api/[controller]")]
    [ApiController]
    public class TrainController : ControllerBase
    {
        [HttpGet]
        public ActionResult<IEnumerable<Hub>> GetHubs()
        {
            return HubManager.hubs;
        }
        
        [HttpPost("{bleAddress}/setSpeed")]
        public async Task<ActionResult> SetSpeed(ulong bleAddress, int speed)
        {
            var hub = HubManager.hubs.FirstOrDefault(h => h.bluetoothAddress == bleAddress);
            if (hub == null)
            {
                Console.WriteLine("Hub not found");
                return NotFound();
            }
            await hub.SetMotorSpeed(speed);
            return Ok();
        }
        
        [HttpPost("{bleAddress}/setLightIntensity")]
        public async Task<ActionResult> SetLightIntensity(ulong bleAddress, int lightIntensity)
        {
            var hub = HubManager.hubs.FirstOrDefault(h => h.bluetoothAddress == bleAddress);
            if (hub == null)
            {
                Console.WriteLine("Hub not found");
                return NotFound();
            }
            await hub.SetLightIntensity(lightIntensity);
            return Ok();
        }
        
        [HttpPost("{bleAddress}/setLedColor")]
        public async Task<ActionResult> SetLedColor(ulong bleAddress, int colorCode)
        {
            var hub = HubManager.hubs.FirstOrDefault(h => h.bluetoothAddress == bleAddress);
            if (hub == null)
            {
                Console.WriteLine("Hub not found");
                return NotFound();
            }
            await hub.SetLedColor(colorCode);
            return Ok();
        }
    }
}
