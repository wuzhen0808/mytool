function loadChart(url, canvasId){
    $.ajax({
        url: url,
        success: function(data){
            const ctx = document.getElementById(canvasId);
            new Chart(ctx, data);
        }
    });
}