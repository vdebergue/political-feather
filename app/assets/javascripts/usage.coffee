# data = [ {word: "abc", dates : [123, 124, ...]}, ...]
window.drawWordUsage = (data, isInit = false) ->
    margin = {top : 20, left: 30, right: 70, bottom: 20}
    svg = d3.select(".wordUsage")
    width = parseInt(svg.style("width"), 10) - margin.left - margin.right
    height = parseInt(svg.style("height"), 10) - margin.top - margin.bottom

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

    if isInit
        chart = svg.append("g")
            .attr("class", "chart")
            .attr("transform", "translate(#{margin.left}, #{margin.top})")
        chart.append("path")
            .attr("class", "line")
        addAxis(chart, xAxis, "Time", yAxis, "Tweets/h", width, height)
    else
        chart = svg.select("g.chart")

    word = chart.selectAll(".word")
        .data(data, (d) -> d.word)
        .enter().append("g")
        .attr("class", "word")

    p = word.append("path")
        .attr("class", "line")
        .attr("d", (d) -> line(d.values))
        .style("stroke", (d) -> color(d.word))
        .on("mouseover", () -> 
            elem = d3.select(this)
            chart.selectAll(".line").style("stroke-opacity", 0.2)
            elem.style("stroke-opacity", 1)
            chart.selectAll(".text").style("display", "none")
            chart.selectAll(".text-" + elem.datum().word).style("display", "block")
        )
        .on("mouseout", () ->
            chart.selectAll(".text").style("display", "block")
            chart.selectAll(".line").style("stroke-opacity", 1)
        )

    w = word.append("text")
        .datum((d) -> {"word": d.word, "value": d.values[0]})
        .attr("class", (d) -> "text text-" + d.word)
        .attr("transform", (d) -> "translate( #{width - 20} , #{y(d.value.count)})")
        .attr("x", 3)
        .attr("dy", ".35em")
        .style("fill", (d) -> color(d.word))
        .text((d) -> d.word)

    zoom = d3.behavior.zoom().on("zoom", () ->
        drawAxis(chart, xAxis, yAxis)
        p.attr("d", (d) -> line(d.values))
        w.attr("transform", (d) -> "translate( #{width - 20} , #{y(d.value.count)})")
    ).x(x)
    svg.call(zoom)
    drawAxis(chart, xAxis, yAxis)
    