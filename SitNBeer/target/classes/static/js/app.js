/**
 * SitNBeer
 * Romain Capocasale, Vincent Moulin and Jonas Freiburghaus
 * He-Arc, INF3dlm-a
 * Spring Course
 * 2019-2020
 */

document.addEventListener('DOMContentLoaded', function () {
    var ranges = document.querySelectorAll("input[type=range]")
    M.Range.init(ranges)

    var dropdowns = document.querySelectorAll(".dropdown-trigger")
    let dropdownOptions = {
        'hover': true,
        'constrainWidth': false
    }
    var instances = M.Dropdown.init(dropdowns, dropdownOptions)

    var sideNavs = document.querySelectorAll('.sidenav')
    var instances = M.Sidenav.init(sideNavs);

    let searchBar = document.getElementById('nav-search')
    searchBar.addEventListener('keyup', function (event) {
        event.preventDefault()
        if (event.keyCode === 13 && this.value) {
            let url = new URL(window.location)
            url.pathname = url.pathname.includes('bar') ? 'bar/query' : 'beer/query'
            let params = new URLSearchParams(url.search)
            let searchQuery = `(name:'${this.value}')`
            params.set('search', searchQuery)
            url.searchParams.set('search', params.get('search'))
            url.searchParams.set('size', params.get('size') || 4)
            url.searchParams.set('page', 1)
            window.location.assign(url)
        }
    })
})