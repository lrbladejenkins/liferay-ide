<#include "./basetemplate.ftl">

<#include "./component.ftl">

public class ${classname} extends ${supperclass} {

<#if ( supperclass == "GenericPortlet" ) >
	@Override
	protected void doView(RenderRequest request, RenderResponse response)
		throws IOException, PortletException {

		PrintWriter printWriter = response.getWriter();

		Object customAttr = request.getAttribute("CUSTOM_ATTRIBUTE");

		printWriter.print("Custom Attribute = " + customAttr);
	}
<#elseif ( supperclass == "RenderFilter" ) >
	@Override
	public void init(FilterConfig filterConfig) throws PortletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain)
			throws IOException, PortletException {
		System.out.println("Before filter");
		request.setAttribute("CUSTOM_ATTRIBUTE", "My Custom Attribute Value");
		chain.doFilter(request, response);
		System.out.println("After filter");
	}
</#if>
}