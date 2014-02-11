window.drawActivity = (data, isInit = false) ->
    margin = {top : 20, left: 30, right: 20, bottom: 20}
    svg = d3.select(".activity")
    width = parseInt(svg.style("width"), 10) - margin.left - margin.right
    height = parseInt(svg.style("height"), 10) - margin.top - margin.bottom

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

    if isInit
        chart = svg.append("g")
            .attr("class", "chart")
            .attr("transform", "translate(#{margin.left}, #{margin.top})")
        chart.append("path")
            .attr("class", "line")
        addAxis(chart, xAxis, "Time", yAxis, "Tweets/h", width, height)
    else
        chart = svg.select("g.chart")

    p = chart.select("path").datum(data)
    p.attr("d", line)

    zoom = d3.behavior.zoom().on("zoom", () ->
        drawAxis(chart, xAxis, yAxis)
        p.attr("d", line)
    ).x(x)
    svg.call(zoom)
    drawAxis(chart, xAxis, yAxis)
