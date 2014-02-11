$(document).ready () ->
    feed = new WebSocket(Config.wsUrl)
    feed.onmessage = onNewMessage
    drawHashTagsTop([])
    drawMostActive([])
    drawActivity([], true)
    drawWordUsage([], true)

onNewMessage = (msg) ->
    data = JSON.parse(msg.data)

    if data.hasOwnProperty("hashtags")
        drawHashTagsTop(data.hashtags)
    else if data.hasOwnProperty("mostActive")
        drawMostActive(data.mostActive)
    else if data.hasOwnProperty("wordUsage")
        drawWordUsage(data.wordUsage)
    else if data.hasOwnProperty("tweetNumber")
        $("#tweetNumber").html(data.tweetNumber)
    else if data.hasOwnProperty("sentiments")
        drawSentiment(data.sentiments)
    else if data.hasOwnProperty("activity")
        drawActivity(data.activity)

window.count = (d) -> 
    d.count

window.drawAxis = (chart, xAxis, yAxis) ->
    chart.select("g.axis.x").call(xAxis)
    chart.select("g.axis.y").call(yAxis)

window.addAxis = (chart, xAxis, xTitle, yAxis, yTitle, width, height) ->
    chart.select("g.axis.x").remove()
    chart.select("g.axis.y").remove()
    chart.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0, #{height + 1})")
        .call(xAxis)
        .append("text")
          .attr("class", "label")
          .attr("x", width)
          .attr("y", -6)
          .style("text-anchor", "end")
          .text(xTitle);
    chart.append("g")
        .attr("class", "y axis")
        .attr("transform", "translate(0, 0)")
        .call(yAxis)
        .append("text")
          .attr("class", "label")
          .attr("transform", "rotate(-90)")
          .attr("y", 6)
          .attr("dy", ".71em")
          .style("text-anchor", "end")
          .text(yTitle)

# [date1, date1, ...] => [{date: date1, count: 2}, ...]
window.groupDates = (dates) ->
    map = {}
    for date in dates
        # set the minutes, seconds and milli to zero
        rounded = Math.floor(+date / 1000.0 / 3600) * 1000 * 3600
        map[rounded] = (map[rounded] || 0) + 1

    out = []
    for k, v of map
        out.push({date: k, count: v})
    out
    

