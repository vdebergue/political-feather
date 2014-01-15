$(document).ready () ->
    feed = new WebSocket(Config.wsUrl)
    feed.onmessage = onNewMessage
    drawHashTagsTop([], true)

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


drawHashTagsTop = (data, isInit = false) ->
    margin = {top : 20, left: 10, right: 0, bottom:0}
    chart = d3.select(".hashtags")
    width = parseInt(chart.style("width"), 10)
    height = parseInt(chart.style("height"), 10)
    barHeight = (height - margin.top - margin.bottom) / data.length

    x = d3.scale.linear()
        .domain([0, d3.max(data, (d) -> d.count)])
        .range([margin.left, width - margin.right])
    xAxis = d3.svg.axis().scale(x).orient("top")
    window.xAxis = xAxis
    
    y = d3.scale.linear()
        .domain([0, data.length])
        .range([margin.top, height - margin.bottom])

    rect = chart.selectAll("rect").data(data, (d) -> d.hashtag)

    rect.enter().insert("rect")
        .attr("x", x(0))
        .attr("y", (d,i) -> y(i))
        .attr("width", (d) -> x(d.count))
        .attr("height", barHeight - 1)

    rect.transition()
        .duration(1000)
        .attr("y", (d,i) -> y(i))
        .attr("width", (d) -> x(d.count))

    rect.exit().remove()

    text = chart.selectAll("text").data(data, (d) -> d.hashtag)

    text.enter().insert("text")
        .attr("x", x(0) + 3)
        .attr("y", (d,i) -> y(i) + y(1) / 2 - 5)
        .text((d) -> "#" + d.hashtag)

    text.transition()
        .duration(1000)
        .attr("y", (d,i) -> y(i) + y(1) / 2 - 5)

    text.exit().remove()

    chart.select("g.x.axis").remove()
    chart.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0, #{margin.top - 1})")
        .call(xAxis)
