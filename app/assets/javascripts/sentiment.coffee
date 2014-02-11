window.drawSentiment = (data) ->
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