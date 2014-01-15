
$(document).ready () ->
    feed = new WebSocket(Config.wsUrl)
    feed.onmessage = onNewMessage
    drawHashTagsTop([])

onNewMessage = (msg) ->
    data = JSON.parse(msg.data)
    console.log(data)

    if data.hasOwnProperty("hashtags")
        console.log("hash")
        drawHashTagsTop(data.hashtags)
    else if data.hasOwnProperty("mostActive")
        console.log("ma")
    else if data.hasOwnProperty("wordUsage")
        console.log("wordusage")

drawHashTagsTop = (data) ->
    chart = d3.select(".hashtags")
    width = parseInt(chart.style("width"), 10)
    height = parseInt(chart.style("height"), 10)
    barHeight = height / data.length;

    x = d3.scale.linear()
        .domain([0, d3.max(data, (d) -> d.count)])
        .range([0, width]);

    rect = chart.selectAll("rect").data(data, (d) -> d.hashtag)

    rect.enter().insert("rect")
        .attr("x", 0)
        .attr("y", (d,i) -> i * barHeight)
        .attr("width", (d) -> x(d.count))
        .attr("height", barHeight - 1)

    rect.transition()
        .duration(1000)
        .attr("y", (d,i) -> i * barHeight)
        .attr("width", (d) -> x(d.count))

    rect.exit().remove()

    text = chart.selectAll("text").data(data, (d) -> d.hashtag)

    text.enter().insert("text")
        .attr("x", 1)
        .attr("y", (d,i) -> i * barHeight + barHeight / 2)
        .text((d) -> "#" + d.hashtag)

    text.transition()
        .duration(1000)
        .attr("y", (d,i) -> i * barHeight + barHeight / 2)

    text.exit().remove()
