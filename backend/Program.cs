using TrainController;

var builder = WebApplication.CreateBuilder(args);
builder.WebHost.UseUrls("http://localhost:5091");

builder.Services.AddControllers();

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();
_ = Task.Run(HubManager.ScanForLEGOHubsContinuously);
app.Run();