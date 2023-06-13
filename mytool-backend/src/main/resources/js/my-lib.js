function loadChart(url, canvasId){
    $.ajax({
        url: url,
        success: function(data){
            const ctx = document.getElementById(canvasId);
            new Chart(ctx, data);
        }
    });
}

function openCorpDetail(corpIdInputId) {
    const input = document.getElementById(corpIdInputId)
    window.open('/ui/corp/detail?corpId='+input.value, '_blank')
}