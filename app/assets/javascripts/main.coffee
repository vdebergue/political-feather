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


drawHashTagsTop = (data) ->
    margin = {top : 20, left: 10, right: 10, bottom: 10}
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
        .attr("width", (d) -> x(d.count) - x(0))
        .attr("height", barHeight - 1)

    rect.transition()
        .duration(1000)
        .attr("y", (d,i) -> y(i))
        .attr("width", (d) -> x(d.count) - x(0))
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
    margin = {top : 20, left: 10, right: 10, bottom: 10}
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

    unit = chart.selectAll("g.user").data(data, (d) -> d.user.screenName)

    gEnter = unit.enter().insert("g")
        .attr("class", "user")
        .attr("transform", (d,i) -> "translate( #{x(0)}, #{y(i)})")

    image = {w: 40, h: 40, left : 10}
    image.top = (barHeight - image.h) / 2
        
    gEnter.append("rect")
        .attr("width", (d) -> x(d.count) - x(0))
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
            .attr("width", (d) -> x(d.count) - x(0))
            .attr("height", barHeight - 1)

    unit.exit().remove()

    chart.select("g.x.axis").remove()
    chart.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0, #{margin.top - 1})")
        .call(xAxis)

drawSentiment = (data) ->
    margin = {top : 20, left: 30, right: 20, bottom: 20}
    chart = d3.select(".sentiment")
    width = parseInt(chart.style("width"), 10)
    height = parseInt(chart.style("height"), 10)
    barHeight = (height - margin.top - margin.bottom) / data.length
    
    minDate = d3.min(data, (d) -> d.date)
    maxDate = d3.max(data, (d) -> d.date)

    r = d3.scale.pow().exponent(2)
        .domain([minDate, maxDate])
        .range([3, 12])

    xMax = d3.max(data, (d) -> d.pos)
    yMax = d3.max(data, (d) -> d.neg)
    max = d3.max([xMax, yMax]) 

    x = d3.scale.linear()
        .domain([0, max])
        .range([margin.left, width - margin.right])
    xAxis = d3.svg.axis().scale(x).orient("bottom")
    
    y = d3.scale.linear()
        .domain([0, max])
        .range([height - margin.top, margin.bottom])
    yAxis = d3.svg.axis().scale(y).orient("left")

    colors = ["#a50026","#d73027","#f46d43","#fdae61","#fee090","#ffffbf","#e0f3f8","#abd9e9","#74add1","#4575b4","#313695"].reverse()
    color = d3.scale.linear()
         .domain(d3.range(0, 1, 1.0 / (colors.length - 1)))
         .range(colors)
    c = d3.scale.linear().domain(d3.extent(data, (d) -> d.date)).range([0,1]);

    # Axis Display
    chart.select("g.axis.x").remove()
    chart.select("g.axis.y").remove()
    chart.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0, #{width - margin.bottom + 1})")
        .call(xAxis)
        .append("text")
          .attr("class", "label")
          .attr("x", width)
          .attr("y", -6)
          .style("text-anchor", "end")
          .text("Positive Score");
    chart.append("g")
        .attr("class", "y axis")
        .attr("transform", "translate(#{margin.left}, 0)")
        .call(yAxis)
        .append("text")
          .attr("class", "label")
          .attr("transform", "rotate(-90)")
          .attr("y", 6)
          .attr("dy", ".71em")
          .style("text-anchor", "end")
          .text("Negative Score")

    dots = chart.selectAll(".dot")
        .data(data.filter((d) -> !(d.pos == 0 && d.neg == 0)), (d) -> d.tweet_id)

    dots.enter()
        .append("circle")
            .attr("class", "dot")
            .attr("r", (d) -> r(d.date))
            .attr("cx", (d) -> x(d.pos))
            .attr("cy", (d) -> y(d.neg))
            .style("fill", (d) -> color(c(d.date)))
            .on("click", displayTweet)

    dots.transition()
        .duration(1000)
        .select("circle")
        .attr("r", (d) -> r(d.date))
        .attr("cx", (d) -> x(d.pos))
        .attr("cy", (d) -> y(d.neg))
        .style("fill", (d) -> color(c(d.date)))

    dots.exit().remove()


displayTweet = (d) ->
    $("#tweet").fadeOut()
    $.get(Config.tweetUrl + d.tweet_id, (resp) ->
        msg = if resp.errors then "<p>Sorry, this tweet does not seem to exist anymore ...</p>" else resp.html
        $("#tweet").html(msg)
        $("#tweet").fadeIn()
    )

# data = [ {word: "abc", dates : [123, 124, ...]}, ...]
drawWordUsage = (data, isInit = false) ->
    margin = {top : 20, left: 30, right: 70, bottom: 20}
    svg = d3.select(".wordUsage")
    width = parseInt(svg.style("width"), 10) - margin.left - margin.right
    height = parseInt(svg.style("height"), 10) - margin.top - margin.bottom

    if isInit
        chart = svg.append("g")
            .attr("class", "chart")
            .attr("transform", "translate(#{margin.left}, #{margin.top})")
        chart.append("path")
            .attr("class", "line")
    else
        chart = svg.select("g.chart")

    data = data.map((w) -> 
        "word" : w.word
        "values" : groupDates(w.dates)
    ) 

    minDate = d3.min(data, (d) -> d3.min(d.values, (d) -> d.date))
    maxDate = d3.max(data, (d) -> d3.max(d.values, (d) -> d.date))
    maxCount = d3.max(data, (d) -> d3.max(d.values, (d) -> d.count))

    x = d3.time.scale()
        .domain([new Date(+minDate), new Date(+maxDate)])
        .range([0, width])
    xAxis = d3.svg.axis().scale(x).orient("bottom")
    
    y = d3.scale.linear()
        .domain([0, maxCount])
        .range([height, 0])
    yAxis = d3.svg.axis().scale(y).orient("left")

    line = d3.svg.line()
        .x((d) -> x(new Date(+d.date)))
        .y((d) -> y(d.count))
        .interpolate("basis")

    color = d3.scale.category10()
        .domain(data.map((w) -> w.word))

    word = chart.selectAll(".word")
        .data(data, (d) -> d.word)
        .enter().append("g")
        .attr("class", "word")

    word.append("path")
        .attr("class", "line")
        .attr("d", (d) -> line(d.values))
        .style("stroke", (d) -> color(d.word))

    word.append("text")
        .datum((d) -> {"word": d.word, "value": d.values[0]})
        .attr("transform", (d) -> "translate( #{x(d.value.date)} , #{y(d.value.count)})")
        .attr("x", 3)
        .attr("dy", ".35em")
        .style("fill", (d) -> color(d.word))
        .text((d) -> d.word)

    addAxis(chart, xAxis, "Time", yAxis, "Count", width, height)

addAxis = (chart, xAxis, xTitle, yAxis, yTitle, width, height) ->
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
groupDates = (dates) ->
    map = {}
    for date in dates
        # set the minutes, seconds and milli to zero
        rounded = Math.floor(+date / 1000.0 / 3600) * 1000 * 3600
        map[rounded] = (map[rounded] || 0) + 1

    out = []
    for k, v of map
        out.push({date: k, count: v})
    out
    
drawActivity = (data, isInit = false) ->
    margin = {top : 20, left: 30, right: 20, bottom: 20}
    svg = d3.select(".activity")
    width = parseInt(svg.style("width"), 10) - margin.left - margin.right
    height = parseInt(svg.style("height"), 10) - margin.top - margin.bottom

    if isInit
        chart = svg.append("g")
            .attr("class", "chart")
            .attr("transform", "translate(#{margin.left}, #{margin.top})")
        chart.append("path")
            .attr("class", "line")
    else
        chart = svg.select("g.chart")

    data = groupDates(data)

    extentDate = d3.extent(data, (d) -> d.date)
    extentCount = d3.extent(data, count)
    x = d3.time.scale()
        .domain([new Date(+extentDate[0]), new Date(+extentDate[1])])
        .rangeRound([0, width])
    xAxis = d3.svg.axis().scale(x).orient("bottom")
    
    y = d3.scale.linear()
        .domain(extentCount)
        .range([height, 0])
    yAxis = d3.svg.axis().scale(y).orient("left")

    line = d3.svg.line()
            .x((d) -> x(new Date(+d.date)))
            .y((d) -> y(d.count))
            .interpolate("basis")

    p = chart.select("path").datum(data)

    p.attr("d", line)

    addAxis(chart, xAxis, "Time", yAxis, "Count", width, height)
