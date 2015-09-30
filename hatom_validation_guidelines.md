# Introduction #
The hAtom microformat version used for the requisites is [draft 0.1](http://microformats.org/wiki/hatom#Schema), with the following modifications:

  * feed-key element has been added under hfeed node.
  * entry-key element has been added under hentry node.

# Requisites #
Validation requisites are listed for each hAtom schema element, in the order specified in the **Schema** and **Field and Element** details of the specification page.

Basic requisites of a xhtml hAtom file for a correct validation  are: being a well-formed XHTML file. Since validator module uses Java SE DOM classes to analyze the document, IDEs will return an error message such as "Unable to load file" if they are given a malformed file or a non-xhtml file.

## hfeed ##
It'a optional element. If missing, the whole XHTML page will be considered a hfeed. Validator modules handles the cases of missing, single or multiple hfeeds.

There are cases of multiple semantic in which two hatom elements are joined in a single attribute as in:   `<div class="hfeed hentry story">`; for this reason a hfeed element, when found, is passed to the hentry analyzer class of the module, in order to find if the hfeed is also a hentry. Validator also checks that there are **no nested hfeed elements**.

## feed-key ##
**Required**. It's a child of hfeed and MUST be unique in the whole feed. It is usually found in the form `<span class="feed-key" title="unique_value" />`. Validator checks that the feed-key is present and not empty; it also checks that the **feed-key value** (ie: title="value") **is unique** in case of multiple hfeed pages and that it is placed **before any hentry element**.

## hentry ##
Hentry element is not defined as mandatory, that's why the module validates a hfeed document that does not contains any hentry element.

Validator checks that hentry has all the three (¹) mandatory child elements: title, updated, author, that there are neither nested hentries nor hfeed elements inside.

In case there are also non-hentry html tags at hentry's same level, validator check that they do not contain invalid hAtom elements.

## entry-key ##
**Required**. It's a child element of hentry and MUST be unique inside the hentry element. It is usually found in the form `<span class="entry-key" title="unique_value" />`. Validator checks that the entry-key is present and not empty; it also checks that the entry-key value is unique between the various hfeed hentries**.**

## entry-title ##
Hatom specification marks this element as mandatory, but this has a rather ambiguous meaning, since it also states that it can also be the first h# element in the hentry, the `<title>` tag of the page, or even an empty string! This seems to be a definition made from the parser's point of view. Right now, the validator implementation **marks the missing entry-title as en error**.

## entry-content and entry-summary ##
They are optional elements, validator just checks that they are properly written as values of a class attribute.

## entry permalink ##
It's not a mandatory element; validator just checks that, if present, it is a properly written rel="bookmark" attribute.

## updated and published ##
Again, the specification is not clear about **updated** mandatory element, stating that if it's missing, the **published** element can be taken as a correct value; but published is an optional element. The current implementation of the validator **marks the missing updated as an error**.

Proposed modification:  validator will mark as invalid hAtom entries that have neither update nor published: the other side of this solution is that the meaning of mandatory/optional definition becomes confused.

As for the **datetime pattern**, validator just validates the datetime string format but not its real value. Value datetime validation doesn't seem mandated by the specification.

Validator also checks that updated/published element are inside an `<abbr>` html tag.

## author ##
Author is a mandatory element. Current validator implementation makes a **minimal check of the hCard format validation**, which is intended as follows:

  * author must appear together with a **vcard** attribute value inside a class attribute (ie: class="vcard author")
  * hCard format has a **fn** property, since n property can be implied in the first one for author's names.

Implementation of nearest-in-parent  has not yet been done: it has been proposed for cancellation in the next spec draft revision (0.2) for it implies a misuse of the ` <address> ` tag. Right now, validator marks the missing author as en error. As a marginal consideration, using it seems to allow just one author in the whole (h)feed, if only one hentry or hfeed has an author.

# Handling non-hAtom tags #
All the non-hAtom tags are traversed by the validator task and inspected in order to discover invalid uses of hAtom microformats.

Ie: if a tag outside an hfeed element is a hAtom element, it marked as error because it violates the hAtom schema.

**NOTES**
(¹) There are other elements that are marked as mandatory somewhere, but NOT in the general Schema, so they have been considered optional.