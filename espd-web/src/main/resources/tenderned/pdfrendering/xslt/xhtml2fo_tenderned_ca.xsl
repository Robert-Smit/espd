<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="xhtml2fo.xsl"/>

	<xsl:attribute-set name="body">
		<!--  specified on fo:flow's only child fo:block  -->
		<xsl:attribute name="font-family">Verdana, Myriad Web, Syntax, sans-serif</xsl:attribute>
		<xsl:attribute name="font-size-adjust">0.30</xsl:attribute>
		<xsl:attribute name="line-height.minimum">1.5em</xsl:attribute>
		<xsl:attribute name="line-height.optimum">1.58em</xsl:attribute>
		<xsl:attribute name="line-height.maximum">1.67em</xsl:attribute>
		<xsl:attribute name="padding-start">12pt</xsl:attribute>
		<xsl:attribute name="padding-end">12pt</xsl:attribute>
		<xsl:attribute name="start-indent">12pt</xsl:attribute>
		<xsl:attribute name="end-indent">12pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="bold-text">
		<xsl:attribute name="font-weight">600</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="espd-panel-heading">
		<xsl:attribute name="background-color">rgb(4,102,164)</xsl:attribute>
		<xsl:attribute name="color">rgb(255,255,255)</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
	</xsl:attribute-set>

	<xsl:template match="div[@id='separate_espd_div']"/>


	<!-- templates used for checking values in text elements, if value is '', there will be shown a dash -->
	<xsl:template name="check-value">
		<xsl:choose>
			<xsl:when test="@value = ''">false</xsl:when>
			<xsl:otherwise>true</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="fill-in-value">
		<xsl:variable name="value-is-filled">
			<xsl:call-template name="check-value"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$value-is-filled = 'true'">
				<xsl:value-of select="@value"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>-</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="input[@type='text']">
		<xsl:call-template name="fill-in-value"/>
	</xsl:template>

	<xsl:template match="textarea">
		<xsl:choose>
			<xsl:when test=". != ''">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:otherwise>
				<fo:block>
					-
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- these templates are used to find the country that is selected, if no country is selected a dash will show -->

	<xsl:template name="country-has-value">
		<xsl:choose>
			<xsl:when test="./option[@selected = 'selected']">false</xsl:when>
			<xsl:otherwise>true</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="select-country">
		<xsl:variable name="country-val">
			<xsl:call-template name="country-has-value"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$country-val = 'false'">
				<xsl:text>-</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<fo:block>
					<xsl:value-of select="./optgroup//option[@selected='selected']"/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="select">
		<xsl:variable name="type-of-select">
			<xsl:value-of select="@name"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$type-of-select = 'paymentTaxes.currency'">
				<xsl:value-of select="./option[@selected='selected']"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="select-country"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="input-is-text">
		<xsl:choose>
			<xsl:when test="@type = 'text'">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="input-is-radiobutton">
		<xsl:choose>
			<xsl:when test="@type = 'radio'">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="input-is-checkbox">
		<xsl:choose>
			<xsl:when test="@type = 'checkbox'">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="input">
		<xsl:variable name="contains-text">
			<xsl:call-template name="input-is-text"/>
		</xsl:variable>
		<xsl:variable name="contains-radiobutton">
			<xsl:call-template name="input-is-radiobutton"/>
		</xsl:variable>
		<xsl:variable name="contains-checkbox">
			<xsl:call-template name="input-is-checkbox"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$contains-text = 'true'">
				<xsl:call-template name="fill-in-value"/>
			</xsl:when>
			<xsl:when test="$contains-radiobutton = 'true'">
				<fo:block/>
				<fo:inline font-family='ZapfDingbats'>&#x274D; </fo:inline>
				<xsl:value-of select="span"/>
			</xsl:when>
			<xsl:when test="$contains-checkbox = 'true'">
				<fo:block/>
				<fo:inline font-family='ZapfDingbats'>&#x274F;</fo:inline>
				<xsl:value-of select="span"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="label/span">
		<fo:block xsl:use-attribute-sets="bold-text">
			<xsl:value-of select="."/>
		</fo:block>
	</xsl:template>

	<xsl:template match="div[@class='espd-panel-heading']">
		<fo:block xsl:use-attribute-sets="espd-panel-heading">
			<xsl:value-of select=".//span"/>
		</fo:block>
	</xsl:template>

	<xsl:strip-space elements="*"/>

</xsl:stylesheet>
