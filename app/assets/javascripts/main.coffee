$(document).ready () ->
    feed = new WebSocket(Config.wsUrl)
    feed.onmessage = onNewMessage
    drawHashTagsTop([])
    drawMostActive([])

onNewMessage = (msg) ->
    data = JSON.parse(msg.data)

    if data.hasOwnProperty("hashtags")
        console.log("hash")
        drawHashTagsTop(data.hashtags)
    else if data.hasOwnProperty("mostActive")
        console.log("ma")
        drawMostActive(data.mostActive)
    else if data.hasOwnProperty("wordUsage")
        console.log("wordusage")


drawHashTagsTop = (data) ->
    margin = {top : 20, left: 10, right: 0, bottom:0}
    chart = d3.select(".hashtags")
    width = parseInt(chart.style("width"), 10)
    height = parseInt(chart.style("height"), 10)
    barHeight = (height - margin.top - margin.bottom) / data.length

    x = d3.scale.linear()
        .domain([0, d3.max(data, count)])
        .range([margin.left, width - margin.right])
    xAxis = d3.svg.axis().scale(x).orient("top")
    
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
        .attr("height", barHeight - 1)


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

count = (d) -> 
    d.count

drawMostActive = (data) -> 
    console.log(data)

    margin = {top : 20, left: 10, right: 0, bottom:0}
    chart = d3.select(".mostActive")
    width = parseInt(chart.style("width"), 10)
    height = parseInt(chart.style("height"), 10)
    barHeight = (height - margin.top - margin.bottom) / data.length


    x = d3.scale.linear()
        .domain([0, d3.max(data, count)])
        .range([margin.left, width - margin.right])
    xAxis = d3.svg.axis().scale(x).orient("top")
    
    y = d3.scale.linear()
        .domain([0, data.length])
        .range([margin.top, height - margin.bottom])

    unit = chart.selectAll("g").data(data, (d) -> d.user.screenName)

    gEnter = unit.enter().insert("g")
        .attr("transform", (d,i) -> "translate( #{x(0)}, #{y(i)})")

    image = {w: 50, h: 50, left : 10}
    image.top = (barHeight - image.h) / 2
        
    gEnter.append("rect")
        .attr("width", (d) -> x(d.count))
        .attr("height", barHeight - 1)
    gEnter.append("text")
        .attr("y", barHeight / 2 - 5)
        .attr("x", 10 + image.w + 10)
        .text((d) -> d.user.name)
    gEnter.append("a")
            .attr("xlink:href", (d) -> "https://twitter.com/" + d.user.screenName)
            .attr("target", "_blank")
            .attr("class", "screenName")
        .append("text")
            .attr("y", barHeight / 2 + 5 + 10)
            .attr("x", 10 + image.w + 20)
            .text((d) -> "@" + d.user.screenName)
    gEnter.append("image")
        .attr("y", image.top)
        .attr("x", image.left)
        .attr("width", image.w)
        .attr("height", image.h)
        .attr("xlink:href", (d) -> d.user.profileImageUrl)

    unit.transition()
        .duration(1000)
        .attr("transform", (d,i) -> "translate( #{x(0)}, #{y(i)})")
        .select("rect")
            .attr("width", (d) -> x(d.count))
            .attr("height", barHeight - 1)

    unit.exit().remove()

    # chart.select("g.x.axis").remove()
    # chart.append("g")
    #     .attr("class", "x axis")
    #     .attr("transform", "translate(0, #{margin.top - 1})")
    #     .call(xAxis)
